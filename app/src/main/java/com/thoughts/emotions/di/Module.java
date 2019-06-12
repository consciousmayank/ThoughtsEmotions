package com.thoughts.emotions.di;

import android.content.Context;
import android.view.LayoutInflater;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.thoughts.emotions.arch.ViewFactory;
import com.thoughts.emotions.screens.login.LoginController;
import com.thoughts.emotions.screens.login.LoginUseCase;
import com.thoughts.emotions.screens.login.RouterLogin;
import com.thoughts.emotions.screens.login.network.LoginApi;
import com.thoughts.emotions.utils.SharedPrefs;

public class Module {

  private final HigherLevelModule compRoot;
  private final AppCompatActivity activity;

  public Module(HigherLevelModule compositionRoot, AppCompatActivity activity) {
    compRoot = compositionRoot;
    this.activity = activity;
  }

  private AppCompatActivity getActivity() {
    return activity;
  }

  private Context getContext() {
    return activity;
  }

  private LayoutInflater getLayoutInflater() {
    return LayoutInflater.from(getContext());
  }

  public SharedPrefs getPreferences() {
    return compRoot.getPrefs();
  }

  public ViewFactory getViewFactory() {
    return new ViewFactory(getLayoutInflater());
  }

  public LoginController getLoginController(SharedPrefs prefs) {
    return new LoginController(getRouterLogin(), prefs, getLoginUseCase(), getFragmentManager());
  }

  private LoginUseCase getLoginUseCase() {
    return new LoginUseCase(getLoginApi());
  }

  private LoginApi getLoginApi() {
      return compRoot.getLoginApi();
  }

  private RouterLogin getRouterLogin() {
    return new RouterLogin(activity);
  }

  private FragmentManager getFragmentManager() {
    return getActivity().getSupportFragmentManager();
  }
}
