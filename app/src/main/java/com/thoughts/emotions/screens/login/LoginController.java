package com.thoughts.emotions.screens.login;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.thoughts.emotions.arch.LazyInit;
import com.thoughts.emotions.progressdialog.ProgressDialogFragment;
import com.thoughts.emotions.utils.SharedPrefs;

public class LoginController implements LoginView.Listeners, LoginUseCase.Listener {

  @NonNull
  private final RouterLogin router;
  private final ProgressDialogFragment progressDialogFragment;
  private final SharedPrefs sharedPrefs;
  private final LoginUseCase loginUseCase;
  private final FragmentManager fragmentManager;
  @LazyInit
  private LoginView view;

  public LoginController(@NonNull RouterLogin router, SharedPrefs prefs,
      LoginUseCase loginUseCase, FragmentManager fragmentManager) {
    this.router = router;
    this.sharedPrefs = prefs;
    this.loginUseCase = loginUseCase;
    this.fragmentManager = fragmentManager;
    this.progressDialogFragment = ProgressDialogFragment.newInstance();
  }

  void bindView(LoginView view) {
    this.view = view;
  }

  void bindLifeCycle(Lifecycle lifecycle) {
    lifecycle.addObserver(new DefaultLifecycleObserver() {
      @Override public void onStart(@NonNull LifecycleOwner owner) {
        view.registerListener(LoginController.this);
        loginUseCase.registerListener(LoginController.this);
      }

      @Override public void onStop(@NonNull LifecycleOwner owner) {
        view.unregisterListener(LoginController.this);
        loginUseCase.unregisterListener(LoginController.this);
        hideProgressDialog();
      }
    });
  }

  @Override public void initiateLogin(String email, String password) {
    loginUseCase.tryLogin(email, password);
    showProgressDialog();
  }

  @Override public void loginFail(String message) {
    hideProgressDialog();
    if (message != null) {
      Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void loginSuccessful(String email, String password) {
    hideProgressDialog();
    sharedPrefs.setIsUserLoggedIn(true);
    router.toHomeScreen();
  }

  private void hideProgressDialog() {
    if (progressDialogFragment.isVisible()) {
      progressDialogFragment.dismiss();
    }
  }

  private void showProgressDialog() {
    if (!progressDialogFragment.isVisible()) {
      progressDialogFragment.show(fragmentManager, ProgressDialogFragment.class.getName());
    }
  }
}
