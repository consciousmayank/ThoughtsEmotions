package com.thoughts.emotions.network.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import com.google.auto.value.AutoValue;
import com.thoughts.emotions.network.util.Preconditions;
import java.util.Iterator;
import retrofit2.Call;

public class CallQueue {

  private final ArraySet<CallObj> calls = new ArraySet<>();

  @Nullable public CallObj remove(@NonNull Call call) {
    Preconditions.checkNotNull(call, "Call cannot be null");
    synchronized (calls) {
      Iterator<CallObj> iterator = calls.iterator();
      while (iterator.hasNext()) {
        CallObj request = iterator.next();
        if (request.call().equals(call)) {
          iterator.remove();
          return request;
        }
      }
    }
    return null;
  }

  public void add(@NonNull String tag, @NonNull Call call) {
    Preconditions.checkNotNull(tag, "Tag cannot be null");
    Preconditions.checkNotNull(call, "Call cannot be null");

    CallObj request = CallObj.create(tag, call);
    synchronized (calls) {
      calls.add(request);
    }
  }

  public void cancel(@NonNull final String tag) {
    Preconditions.checkNotNull(tag, "Tag cannot be null");
    synchronized (calls) {
      for (CallObj request : calls) {
        if (request != null && request.tag().equals(tag)) {
          request.call().cancel();
        }
      }
    }
  }

  @AutoValue
  abstract static class CallObj {

    abstract String tag();

    abstract Call call();

    static CallObj create(String tag, Call call) {
      return new AutoValue_CallQueue_CallObj(tag, call);
    }
  }
}
