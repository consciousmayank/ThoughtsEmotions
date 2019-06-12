package com.thoughts.emotions.screens.login;

import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.BindView;
import com.google.android.material.textfield.TextInputLayout;
import com.thoughts.emotions.R;
import com.thoughts.emotions.arch.BaseActivity;

public class LoginActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LoginView loginView =  getCompositionRoot().getViewFactory().getLoginView();
    LoginController loginController = getCompositionRoot().getLoginController(prefs);
    loginController.bindView(loginView);
    loginController.bindLifeCycle(getLifecycle());
    setContentView(loginView.getRootView());
    getSupportActionBar().setTitle("Please Login");
  }
}
