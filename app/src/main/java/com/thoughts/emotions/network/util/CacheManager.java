package com.thoughts.emotions.network.util;

import android.content.Context;
import android.net.Uri;
import android.util.LruCache;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.thoughts.emotions.network.util.thread.BackgroundWorker;
import com.thoughts.emotions.network.util.thread.MainThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Future;
import timber.log.Timber;

public class CacheManager {

  private static final int CACHE_SIZE = 50;
  private static final int KRYO_POOL_SIZE = 4;

  private final Context context;

  private final LruCache<String, Object> lruCache;

  private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, KRYO_POOL_SIZE) {
    @Override
    protected Kryo create() {
      return new Kryo();
    }
  };

  public CacheManager(Context context) {
    this.context = context;
    lruCache = new LruCache<>(CACHE_SIZE);
  }

  public void cache(@NonNull final String key, @NonNull final Object value) {
    Preconditions.checkNotNull(value);
    Timber.d("Caching response for %s", key);

    lruCache.put(key, value);

    Future outputFuture = BackgroundWorker.instance.post(
        () -> {
          File cacheDir = new File(context.getCacheDir().getAbsolutePath());
          File f = new File(cacheDir, Uri.encode(key));
          if (cacheDir.exists()) {
            Kryo kryo = kryoPool.obtain();
            kryo.register(value.getClass());
            try (Output output = new Output(new FileOutputStream(f))) {
              kryo.writeObject(output, value);
            } catch (IOException e) {
              Timber.d(e, "Failed to cache response for %s", key);
            }
            kryoPool.free(kryo);
          }
        });

    if (!outputFuture.isDone()) {
      Timber.d("there an error in caching");
    }
  }

  //XXX: TODO remove TypeParameterUnusedInFormals
  @SuppressWarnings("TypeParameterUnusedInFormals")
  @Nullable
  @WorkerThread
  public <T> T get(@NonNull final String key, @NonNull final Type type) {
    MainThread.assertNotInMainThread();
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(type);

    Object cachedValue = lruCache.get(key);
    if (cachedValue == null) { //try to read from disk
      File cacheDir = new File(context.getCacheDir().getAbsolutePath());
      File cachedFile = new File(cacheDir, Uri.encode(key));
      if (cachedFile.exists()) {
        Class<?> clazz = (Class<?>) type;

        Kryo kryo = kryoPool.obtain();
        kryo.register(clazz);

        try (Input input = new Input(new FileInputStream(cachedFile))) {
          cachedValue = kryo.readObject(input, clazz);
        } catch (FileNotFoundException e) {
          Timber.d(e, "No cache exists for %s", key);
        }
        kryoPool.free(kryo);
      }
    }
    return (T) cachedValue;
  }

  public void clear() {
    lruCache.evictAll();
    File cacheDir = new File(context.getCacheDir().getAbsolutePath());
    if (cacheDir.exists()) {
      File[] files = cacheDir.listFiles();
      if (files != null) {
        for (File currentFile : files) {
          currentFile.delete();
        }
      }
    }
  }
}
