package com.example.auth1;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> mAuthResults;
    private LiveData<String> mAuthResultsPublic;

    private AuthSingleton authSingleton;

    public LiveData<String> loginMSAL(Activity activity) {
        // Login to MSAL
        authSingleton = AuthSingleton.getInstance(activity);
        mAuthResults = authSingleton.getLiveDataToken();
        mAuthResultsPublic = mAuthResults;
        return mAuthResultsPublic;
    }

    public LiveData<String> getAuthResults() {
        return mAuthResultsPublic;
    }
}
