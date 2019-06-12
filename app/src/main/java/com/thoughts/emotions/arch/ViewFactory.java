package com.thoughts.emotions.arch;

import android.view.LayoutInflater;
import com.thoughts.emotions.screens.login.LoginViewImpl;

public class ViewFactory {

  private final LayoutInflater layoutInflater;

  public ViewFactory(LayoutInflater layoutInflater) {
    this.layoutInflater = layoutInflater;
  }

  public LoginViewImpl getLoginView() {
    return new LoginViewImpl(layoutInflater);
  }
}
