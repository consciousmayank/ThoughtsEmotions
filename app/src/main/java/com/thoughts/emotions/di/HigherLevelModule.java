package com.thoughts.emotions.di;

import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.network.retrofit.CallQueue;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class HigherLevelModule {

  private static final int CACHE_SIZE = 1024 * 1024;


  private final MyApp myApp;

  private Retrofit retrofit;

  private CallQueue callQueue;

  public HigherLevelModule(MyApp myApp) {
    this.myApp = myApp;
  }

  private Retrofit getRetrofit() {
    if (retrofit == null) {

      OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
      httpBuilder.connectTimeout(30, TimeUnit.SECONDS);
      httpBuilder.readTimeout(30, TimeUnit.SECONDS);
      httpBuilder.writeTimeout(30, TimeUnit.SECONDS);
      httpBuilder.cache(new Cache(myApp.getCacheDir(), CACHE_SIZE));

      retrofit = new Retrofit.Builder()
          .client(httpBuilder.build())
          //.baseUrl(Configuration.BASE_URL)
          //.addCallAdapterFactory(getAdapterFactory())
          .addConverterFactory(MoshiConverterFactory.create())
          .build();
    }
    return retrofit;
  }
}
