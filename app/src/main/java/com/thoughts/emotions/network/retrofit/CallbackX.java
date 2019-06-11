package com.thoughts.emotions.network.retrofit;

import androidx.annotation.Nullable;

public interface CallbackX<R, E> {

  void onResponse(R response);

  void onFailure(@Nullable E errorResponse, Throwable throwable);
}
