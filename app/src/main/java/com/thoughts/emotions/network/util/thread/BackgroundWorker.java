package com.thoughts.emotions.network.util.thread;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.thoughts.emotions.network.util.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public enum BackgroundWorker {
  instance;

  private static class ScheduledExecutorDelegate {
    private static final int THREAD_COUNT = GlideExecutor.calculateBestThreadCount();
    static final ScheduledThreadPoolExecutor INSTANCE =
        new ScheduledThreadPoolExecutor(THREAD_COUNT);
  }

  private static class ExecutorDelegate {
    private static final String THREAD_NAME = "common";
    private static final int THREAD_COUNT = GlideExecutor.calculateBestThreadCount();

    static final ThreadPoolExecutor INSTANCE =
        new ThreadPoolExecutor(
            THREAD_COUNT /* corePoolSize */,
            THREAD_COUNT /* maximumPoolSize */,
            0 /* keepAliveTime */,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new DefaultThreadFactory(THREAD_NAME, new UncaughtThrowableStrategy()));
  }

  BackgroundWorker() {
  }

  public Future<?> post(@NonNull Runnable runnable) {
    return ExecutorDelegate.INSTANCE.submit(Preconditions.checkNotNull(runnable));
  }

  public final ScheduledFuture<?> postDelayed(
      @NonNull String tag, @NonNull Runnable runnable, long delay, @NonNull TimeUnit timeUnit) {
    return ScheduledExecutorDelegate.INSTANCE.schedule(
        Preconditions.checkNotNull(runnable), delay,
        Preconditions.checkNotNull(timeUnit));
  }

  public void shutdown() {
    ExecutorDelegate.INSTANCE.shutdown();
    ScheduledExecutorDelegate.INSTANCE.shutdown();
  }

  static final class DefaultThreadFactory implements ThreadFactory {
    private static final int DEFAULT_PRIORITY = android.os.Process.THREAD_PRIORITY_BACKGROUND
        + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

    private final String name;
    final UncaughtThrowableStrategy uncaughtThrowableStrategy;
    private int threadNum;

    DefaultThreadFactory(String name, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
      this.name = name;
      this.uncaughtThrowableStrategy = uncaughtThrowableStrategy;
    }

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    @Override
    public synchronized Thread newThread(@NonNull Runnable runnable) {
      final Thread result = new Thread(runnable, name + "-thread-" + threadNum) {
        @Override
        public void run() {
          android.os.Process.setThreadPriority(DEFAULT_PRIORITY);
          try {
            super.run();
          } catch (Throwable t) { //NOPMD AvoidCatchingThrowable
            uncaughtThrowableStrategy.handle(t);
          }
        }
      };
      threadNum++;
      return result;
    }
  }

  static class UncaughtThrowableStrategy {
    void handle(Throwable t) {
      if (t != null) {

        RuntimeException exception = new RuntimeException(t);
        exception.setStackTrace(t.getStackTrace());

        throw exception;
      }
    }
  }
}
