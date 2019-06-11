package com.thoughts.emotions.network.util.thread;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

public final class MainThread {

  private MainThread() {}

  private static final Handler HANDLER = new Handler(Looper.getMainLooper());

  public static void execute(@NonNull Runnable command) {
    HANDLER.post(command);
  }

  public static void assertNotInMainThread() {
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      throw new AssertionError();
    }
  }
}
