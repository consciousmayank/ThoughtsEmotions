package com.thoughts.emotions;

import android.app.Application;
import com.thoughts.emotions.di.HigherLevelModule;

public class MyApp extends Application {

  private HigherLevelModule compRoot;

  @Override public void onCreate() {
    super.onCreate();
    compRoot = new HigherLevelModule(this);
  }

  public HigherLevelModule getCompRoot() {
    return compRoot;
  }
}
