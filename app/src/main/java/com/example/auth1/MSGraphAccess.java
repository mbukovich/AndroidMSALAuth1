package com.example.auth1;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * This Class makes HTTP calls to the Microsoft Graph API
 */
public class MSGraphAccess {
    public static final String HOST_URL = "https://graph.microsoft.com/v1.0/";

    public static String getUserDisplayName(String accessToken) {
        // Make MSGraph call to get the logged in User's Display Name

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .build();

        accessToken = "Bearer " + accessToken;
        String displayName = "";
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
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
            e.printStackTrace();
        }
        return displayName;
    }
}
