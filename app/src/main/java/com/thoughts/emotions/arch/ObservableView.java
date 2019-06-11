package com.thoughts.emotions.arch;

public interface ObservableView<T> extends ViewX {

  void registerListener(T listener);

  void unregisterListener(T listener);
}
