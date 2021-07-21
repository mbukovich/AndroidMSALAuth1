package com.example.auth1;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * This Class makes HTTP calls to the Microsoft Graph API
 */
public class MSGraphAccess {
    public static final String HOST_URL = "https://graph.microsoft.com/";

    public static String getUserDisplayName(String accessToken) {
        // Make MSGraph call to get the logged in User's Display Name
        String displayName = "";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MSGraphEndpoints endpoints = retrofit.create(MSGraphEndpoints.class);
        Call<User> call = endpoints.getMe(accessToken);

        try {
            Response<User> response = call.execute();
            displayName = response.body().getDisplayName();
        } catch (Exception e) {
            Log.e("MSGRAPH_ERROR", e.getMessage());
        }
        return displayName;
    }
}
