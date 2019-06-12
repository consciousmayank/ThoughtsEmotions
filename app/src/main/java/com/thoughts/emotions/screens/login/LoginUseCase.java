package com.thoughts.emotions.screens.login;

import androidx.annotation.Nullable;
import com.thoughts.emotions.arch.BaseObservable;
import com.thoughts.emotions.network.retrofit.CallbackX;
import com.thoughts.emotions.screens.login.network.LoginApi;
import com.thoughts.emotions.screens.login.network.OtpHttpError;
import com.thoughts.emotions.screens.login.network.schema.UserDetails;
import java.util.Map;

public class LoginUseCase extends BaseObservable<LoginUseCase.Listener> {

  private final LoginApi loginApi;

  public LoginUseCase(LoginApi loginApi) {
    this.loginApi = loginApi;
  }

  public void tryLogin(String email, String password) {
    loginApi.
        tryLogin().
        enqueue("Login", new CallbackX<Map<String, UserDetails>, OtpHttpError>() {
          @Override public void onResponse(Map<String, UserDetails> response) {
            for (Map.Entry<String, UserDetails> entry : response.entrySet()) {
              if (entry.getValue().getPassword().equalsIgnoreCase(password) && entry.getValue()
                  .getUserName()
                  .equalsIgnoreCase(email)) {
                loginSuccessful(email, password);
                return;
              }
            }
            loginFailure("UserName/Password not matched! Retry.");
          }

          @Override
          public void onFailure(@Nullable OtpHttpError errorResponse, Throwable throwable) {
            loginFailure(errorResponse == null ? throwable.getMessage()
                : errorResponse.getResponseMessage());
          }
        });
  }

  private void loginFailure(String message) {
    for (LoginUseCase.Listener listener :
        getListeners()) {
      listener.loginFail(message);
    }
  }

  private void loginSuccessful(String email, String password) {
    for (LoginUseCase.Listener listener :
        getListeners()) {
      listener.loginSuccessful(email, password);
    }
  }

  public interface Listener {
    void loginSuccessful(String email, String password);

    void loginFail(String message);
  }
}
