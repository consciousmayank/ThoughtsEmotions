package com.thoughts.emotions.network.retrofit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

public interface CallX<R, E> {

  void enqueue(@NonNull String tag, @NonNull CallbackX<R, E> callback);

  void enqueue(@NonNull String tag, @NonNull CallbackX<R, E> callback,
      @NonNull LifecycleOwner owner);

  void cancel();
}
