package com.thoughts.emotions.screens.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.android.material.textfield.TextInputLayout;
import com.thoughts.emotions.R;
import com.thoughts.emotions.arch.BaseObservableView;

public class LoginViewImpl extends BaseObservableView<LoginView.Listeners> implements LoginView {

  @BindView(R.id.email_textInputLayout) TextInputLayout email;
  @BindView(R.id.password_textInputLayout) TextInputLayout password;
  @BindView(R.id.login_button) Button loginBtn;

  public LoginViewImpl(LayoutInflater inflater) {
    setRootView(inflater.inflate(R.layout.activity_login, null, false));
  }

  @OnClick(R.id.login_button)
  public void onLoginButtonClick() {
    if (checkErrors()) {
      initiateLogin(email.getEditText().getText().toString().trim(),
          password.getEditText().getText().toString().trim());
    }
  }

  private boolean checkErrors() {
    return email.getEditText().getText().toString().trim().length() > 0
        && password.getEditText().getText().toString().trim().length() > 0;
  }

  private void initiateLogin(String email, String password) {
    for (Listeners listeners :
        getListeners()) {
      listeners.initiateLogin(email, password);
    }
  }

  @Override public Context getContext() {
    return getRootView().getContext();
  }
}
