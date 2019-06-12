package com.thoughts.emotions.screens.login.network;

import com.thoughts.emotions.network.EndPoints;
import com.thoughts.emotions.network.retrofit.CallX;
import com.thoughts.emotions.screens.login.network.schema.UserDetails;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LoginApi {

  @Headers("Content-Type: application/json")
  @GET(EndPoints.TRY_LOGIN)
  CallX<Map<String, UserDetails>, OtpHttpError> tryLogin();
}
