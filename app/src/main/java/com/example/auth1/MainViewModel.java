package com.example.auth1;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> mAuthResults;
    private LiveData<String> mAuthResultsPublic;

    private MutableLiveData<String> mUserDisplayName = new MutableLiveData<>();
    private LiveData<String> mUserDisplayNamePublic = mUserDisplayName;

    private AuthSingleton authSingleton;

    public LiveData<String> loginMSAL(Activity activity) {
        // Login to MSAL
        authSingleton = AuthSingleton.getInstance(activity);
        mAuthResults = authSingleton.getLiveDataToken();
        mAuthResultsPublic = mAuthResults;
        return mAuthResultsPublic;
    }

    public void getMSGraphUser(String accessToken) {
        AppExecutors appExecutors = AppExecutors.getInstance();
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                String displayName = MSGraphAccess.getUserDisplayName(accessToken);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mUserDisplayName.setValue(displayName);
                    }
                });
            }
        });
    }

    public LiveData<String> getAuthResults() {
        return mAuthResultsPublic;
    }

    public LiveData<String> getUserDisplayName() { return mUserDisplayNamePublic; }
}
