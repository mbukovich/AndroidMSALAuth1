package com.example.auth1;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.GET;

public interface MSGraphEndpoints {
    @GET("me")
    Call<User> getMe(@Header("Authorization") String accessToken);
}
