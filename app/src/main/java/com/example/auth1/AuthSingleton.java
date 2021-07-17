package com.example.auth1;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SimpleTimeZone;

public class AuthSingleton {
    private static AuthSingleton mAuthSingleton;

    private MutableLiveData<String> mAccessToken = new MutableLiveData<>();

    private IMultipleAccountPublicClientApplication mClientApp;
    private IAccount mFirstAccount = null;

    private String[] scopes = {"User.Read"};

    public static AuthSingleton getInstance(Activity activity) {
        if (mAuthSingleton == null) {
            mAuthSingleton = new AuthSingleton(activity);
        }
        return mAuthSingleton;
    }

    private AuthSingleton(Activity activity) {
        // Create Public Client
        AppExecutors executors = AppExecutors.getInstance();
        executors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                createClient(activity);
            }
        });
    }

    public MutableLiveData<String> getLiveDataToken() {
        return mAccessToken;
    }

    private void createClient(Activity activity) {
        PublicClientApplication.createMultipleAccountPublicClientApplication(activity.getApplicationContext(),
                R.raw.msal_config,
                new IPublicClientApplication.IMultipleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(IMultipleAccountPublicClientApplication application) {
                        mClientApp = application;
                        getToken(activity);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        // Log Exception Here
                        Log.e("MSAL_ERROR", exception.getMessage());
                    }
                });
    }

    public void getToken(Activity activity) {

        // get a token. First we try silently, and catch any error with an interactive call

        try {
            IAccount account = mClientApp.getAccount(mFirstAccount.getId());
            AcquireTokenSilentParameters parameters = new AcquireTokenSilentParameters.Builder()
                    .forAccount(account)
                    .fromAuthority(mClientApp.getConfiguration().getAuthorities().get(0).getAuthorityURL().toString())
                    .withScopes(Arrays.asList(scopes.clone()))
                    .build();
            IAuthenticationResult result = mClientApp.acquireTokenSilent(parameters);
        } catch (Exception e) {
            // Interactive call
            try {

                mClientApp.acquireToken(activity, scopes, new AuthenticationCallback() {
                    @Override
                    public void onCancel() {
                        String message = "Cancel: User Cancelled Authentication.";
                        mAccessToken.setValue(message);
                    }

                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        mAccessToken.setValue(authenticationResult.getAccessToken());
                        mFirstAccount = authenticationResult.getAccount();
                        Log.d("MSAL_GOT_TOKEN", "Access Token acquired.");
                    }

                    @Override
                    public void onError(MsalException exception) {
                        String message = "error: " + exception.getMessage();
                        mAccessToken.setValue(message);
                    }
                });

            } catch (Exception e1) {
                Log.e("MSAL_SILENT", e.getMessage());
                Log.e("MSAL_INTERACTION", e1.getMessage());
            }
        }
    }
}
