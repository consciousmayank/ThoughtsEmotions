package com.thoughts.emotions.di;

import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.arch.FancyCallAdapterFactory;
import com.thoughts.emotions.network.EndPoints;
import com.thoughts.emotions.network.retrofit.CallQueue;
import com.thoughts.emotions.network.retrofit.cache.CacheAdapter;
import com.thoughts.emotions.network.retrofit.cache.RealCacheAdapter;
import com.thoughts.emotions.network.util.CacheManager;
import com.thoughts.emotions.screens.login.network.LoginApi;
import com.thoughts.emotions.utils.SharedPrefs;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class HigherLevelModule {

  private static final int CACHE_SIZE = 1024 * 1024;


  private final MyApp myApp;

  private Retrofit retrofit;

  private CallQueue callQueue;

  private SharedPrefs prefs;
  private CacheAdapter cacheAdapter;

  public HigherLevelModule(MyApp myApp) {
    this.myApp = myApp;
  }

  private Retrofit getRetrofit() {
    if (retrofit == null) {

      OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
      httpBuilder.addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
          Request original = chain.request();
          HttpUrl originalHttpUrl = original.url();

          HttpUrl url = originalHttpUrl.newBuilder()
              .addQueryParameter("auth", "PEHImZPEKjA17ubjbZa2HfIaHIpLjzlYKyBhrVlY")
              .build();

          // Request customization: add request headers
          Request.Builder requestBuilder = original.newBuilder()
              .url(url);

          Request request = requestBuilder.build();
          return chain.proceed(request);
        }
      });
      httpBuilder.connectTimeout(30, TimeUnit.SECONDS);
      httpBuilder.readTimeout(30, TimeUnit.SECONDS);
      httpBuilder.writeTimeout(30, TimeUnit.SECONDS);
      httpBuilder.cache(new Cache(myApp.getCacheDir(), CACHE_SIZE));

      retrofit = new Retrofit.Builder()
          .client(httpBuilder.build())
          .baseUrl(EndPoints.BASE_URL)
          .addCallAdapterFactory(getAdapterFactory())
          .addConverterFactory(MoshiConverterFactory.create())
          .build();
    }
    return retrofit;
  }

  private CallAdapter.Factory getAdapterFactory() {
    return FancyCallAdapterFactory.create(getCallQueue(), getCacheAdapter());
  }

  public CallQueue getCallQueue() {
    if (callQueue == null) {
      callQueue = new CallQueue();
    }
    return callQueue;
  }

  private CacheAdapter getCacheAdapter() {

    if (cacheAdapter == null) {
      cacheAdapter = new RealCacheAdapter(new CacheManager(myApp));
    }

    return cacheAdapter;
  }


  public SharedPrefs getPrefs() {
    if (prefs!=null) {
      return prefs;
    }else{
      prefs = new SharedPrefs(myApp.getApplicationContext());
      return prefs;
    }
  }

  public LoginApi getLoginApi() {
    return getRetrofit().create(LoginApi.class);
  }
}
