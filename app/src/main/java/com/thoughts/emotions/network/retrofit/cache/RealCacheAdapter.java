package com.thoughts.emotions.network.retrofit.cache;

import com.thoughts.emotions.network.retrofit.CallbackX;
import com.thoughts.emotions.network.util.CacheManager;
import com.thoughts.emotions.network.util.thread.BackgroundWorker;
import com.thoughts.emotions.network.util.thread.MainThread;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Future;
import retrofit2.Call;
import timber.log.Timber;

public class RealCacheAdapter<R, E> implements CacheAdapter<R, E> {

  private final CacheManager cacheManager;

  public RealCacheAdapter(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Override
  public void onEnqueue(CallbackX<R, E> callback,
      Call<R> call, Annotation[] annotations, Type responseType) {
    if (cacheableRespone(annotations)) {
      Future outputFuture = BackgroundWorker.instance.post(() -> {
        R responseModel = cacheManager.get(call.request().url().toString(), responseType);
        if (responseModel != null) {
          MainThread.execute(() -> callback.onResponse(responseModel));
        }
      });
      if (!outputFuture.isDone()) {
        Timber.d("There's an error in reading from cache");
      }
    }
  }

  @Override
  public void onResponse(Call<R> call, Annotation[] annotations,
      R body) {
    if (cacheableRespone(annotations)) {
      cacheManager.cache(call.request().url().toString(), body);
    }
  }

  private boolean cacheableRespone(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(Cacheable.class)) {
        return true;
      }
    }
    return false;
  }
}
