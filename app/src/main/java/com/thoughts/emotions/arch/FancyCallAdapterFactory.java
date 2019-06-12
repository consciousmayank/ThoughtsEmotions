package com.thoughts.emotions.arch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.thoughts.emotions.network.model.InvalidModelResponse;
import com.thoughts.emotions.network.retrofit.CallQueue;
import com.thoughts.emotions.network.retrofit.CallX;
import com.thoughts.emotions.network.retrofit.CallbackX;
import com.thoughts.emotions.network.retrofit.cache.CacheAdapter;
import com.thoughts.emotions.network.util.Preconditions;
import com.thoughts.emotions.network.util.thread.MainThread;
import com.uber.rave.InvalidModelException;
import com.uber.rave.Rave;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class FancyCallAdapterFactory extends CallAdapter.Factory {

  private final CallQueue callQueue;
  private final CacheAdapter cacheAdapter;

  private FancyCallAdapterFactory(CallQueue callQueue, CacheAdapter cacheAdapter) {
    this.callQueue = callQueue;
    this.cacheAdapter = cacheAdapter;
  }

  public static FancyCallAdapterFactory create(CallQueue callQueue,
      CacheAdapter cacheAdapter) {
    return new FancyCallAdapterFactory(callQueue, cacheAdapter);
  }

  private static ResponseBody getEmptyResponseBody() {
    return new ResponseBody() {
      @Nullable
      @Override
      public MediaType contentType() {
        return null;
      }

      @Override
      public long contentLength() {
        return 0;
      }

      @Nullable
      @Override
      public BufferedSource source() {
        return null;
      }
    };
  }

  @Nullable
  @Override
  public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations,
      @NonNull Retrofit retrofit) {

    if (getRawType(returnType) != CallX.class) {
      return null;
    }
    if (!(returnType instanceof ParameterizedType)) {
      throw new IllegalStateException("CallX return type must be parameterized"
          + " as CallX<Foo> or CallX<? extends Foo>");
    }
    Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
    Type errorType = getParameterUpperBound(1, (ParameterizedType) returnType);

    if (getRawType(innerType) != Response.class) {
      // Generic type is not FuturePayWalletBalanceSchema<T>. Use it for body-only adapter.
      return new BodyCallAdapter<>(innerType,
          new ErrorConverter<>(errorType, retrofit, annotations), callQueue, cacheAdapter,
          annotations);
    } else {
      //XXX: test this more
      throw new IllegalStateException("FuturePayWalletBalanceSchema not allowed here");
    }
  }

  private static final class BodyCallAdapter<R, E> implements CallAdapter<R, CallX<R, E>> {

    private final Type resType;
    @Nullable
    private final ErrorConverter<E> errorConverter;
    private final CallQueue callQueue;
    CacheAdapter cacheAdapter;
    Annotation[] annotations;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    BodyCallAdapter(Type resType,
        @Nullable ErrorConverter<E> errorConverter,
        CallQueue callQueue, CacheAdapter cacheAdapter,
        Annotation[] annotations) {
      this.resType = resType;
      this.errorConverter = errorConverter;
      this.callQueue = callQueue;
      this.cacheAdapter = cacheAdapter;
      this.annotations = annotations;
    }

    @Override
    public Type responseType() {
      return resType;
    }

    @Override
    public CallX<R, E> adapt(@NonNull final Call<R> call) {
      return new CallX<R, E>() {
        @Override
        public void enqueue(@NonNull final String tag,
            final @NonNull CallbackX<R, E> callback, @NonNull LifecycleOwner owner) {
          Preconditions.checkNotNull(tag);
          Preconditions.checkNotNull(owner);
          Preconditions.checkNotNull(callback);

          if (owner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED) {
            enqueueInternal(tag,
                new LifecycleCallbackWrapper<>(callback, owner.getLifecycle(), this));
          }
        }

        @Override
        public void enqueue(@NonNull final String tag, @NonNull final CallbackX<R, E> callback) {
          Preconditions.checkNotNull(tag);
          Preconditions.checkNotNull(callback);
          cacheAdapter.onEnqueue(callback, call, annotations, responseType());
          enqueueInternal(tag, callback);
        }

        private void enqueueInternal(@NonNull final String tag, CallbackX<R, E> callback) {
          callQueue.add(tag, call);
          call.enqueue(new BodyCallback<>(errorConverter, callQueue,
              callback, cacheAdapter, annotations));
        }

        @Override
        public void cancel() {
          callQueue.remove(call);
          call.cancel();
        }
      };
    }
  }

  static class LifecycleCallbackWrapper<R, E> implements CallbackX<R, E>, DefaultLifecycleObserver {

    Lifecycle owner;
    CallbackX<R, E> callback;
    CallX<R, E> call;

    LifecycleCallbackWrapper(@NonNull CallbackX<R, E> callback,
        @NonNull Lifecycle owner,
        CallX<R, E> call) {
      this.owner = owner;
      this.callback = callback;
      this.call = call;
      this.owner.addObserver(this);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
      cancel();
    }

    @Override
    public void onFailure(@Nullable E errorResponse, Throwable tu) {
      cancel();
      handleFailure(errorResponse, tu);
    }

    @Override
    public void onResponse(R response) {
      cancel();
      handleResponse(response);
    }

    private void handleResponse(R response) {
      if (owner.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
        callback.onResponse(response);
      }
    }

    private void handleFailure(@Nullable E errorResponse, Throwable t) {
      if (owner.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
        callback.onFailure(errorResponse, t);
      }
    }

    private void cancel() {
      call.cancel();
      owner.removeObserver(this);
    }
  }

  static class BodyCallback<R, E> implements Callback<R> {

    @Nullable
    private final ErrorConverter<E> errorConverter;
    private final CallQueue callQueue;
    private final CallbackX<R, E> callback;
    private final CacheAdapter cacheAdapter;
    private final Annotation[] annotations;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    BodyCallback(
        @Nullable ErrorConverter<E> errorConverter, CallQueue callQueue,
        @NonNull CallbackX<R, E> callback, CacheAdapter cacheAdapter,
        Annotation[] annotations) {
      this.errorConverter = errorConverter;
      this.callQueue = callQueue;
      this.callback = callback;
      this.cacheAdapter = cacheAdapter;
      this.annotations = annotations;
    }

    @Override
    public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {
      MainThread.execute(() -> {
        if (response.isSuccessful()) {
          R body = response.body();
          if (body != null) {
            try {
              Rave.getInstance().validateIgnoreUnsupported(body);
              cacheAdapter.onResponse(call, annotations, body);
              callback.onResponse(body);
            } catch (InvalidModelException e) {
              callback.onFailure(null, new InvalidModelResponse(response));
            }
          } else {
            callback.onFailure(null, new InvalidModelResponse(response));
          }
        } else {
          Response copy = response;
          E errorBody = null;
          if (errorConverter != null && errorConverter.errorType != Void.class) {
            copy = Response.error(getEmptyResponseBody(), response.raw());
            errorBody = errorConverter.convert(response.errorBody());
          }
          callback.onFailure(errorBody, new HttpException(copy));
        }
      });

      callQueue.remove(call);
    }

    @Override
    public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
      callQueue.remove(call);

      if (!call.isCanceled()) {
        MainThread.execute(() -> callback.onFailure(null, t));
      }
    }
  }

  private static class ErrorConverter<E> {
    private final Type errorType;
    private final Retrofit retrofit;
    private final Annotation[] annotations;

    @SuppressWarnings("PMD.UseVarargs")
    ErrorConverter(@NonNull Type errorType,
        @NonNull Retrofit retrofit,
        @Nullable Annotation[] annotations) {
      this.errorType = errorType;
      this.retrofit = retrofit;
      this.annotations = (annotations == null) ? new Annotation[0] : annotations;
    }

    @Nullable
    E convert(@Nullable ResponseBody responseBody) {
      E response = null;
      try {
        if (responseBody != null) {
          Converter<ResponseBody, E> converter =
              retrofit.responseBodyConverter(errorType, annotations);
          response = converter.convert(responseBody);
        }
      } catch (IOException | IllegalArgumentException e) {
        Timber.e(e, "onConvert");
      }
      return response;
    }
  }
}
