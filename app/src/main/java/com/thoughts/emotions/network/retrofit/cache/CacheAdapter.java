package com.thoughts.emotions.network.retrofit.cache;

import com.thoughts.emotions.network.retrofit.CallbackX;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit2.Call;

public interface CacheAdapter<R, E> {
  void onEnqueue(CallbackX<R, E> callback, Call<R> call,
      Annotation[] annotations, Type responseType);

  void onResponse(Call<R> call, Annotation[] annotations, R body);
}
