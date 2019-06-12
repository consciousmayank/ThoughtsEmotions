package com.thoughts.emotions.screens.login;

import android.content.Context;
import com.thoughts.emotions.arch.ObservableView;

public interface LoginView extends ObservableView<LoginView.Listeners> {

  Context getContext();

  public interface Listeners {
    void initiateLogin(String email, String password);
  }
}
