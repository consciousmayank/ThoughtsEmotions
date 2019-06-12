package com.thoughts.emotions.screens.splash;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import android.os.Bundle;
import com.thoughts.emotions.R;
import com.thoughts.emotions.arch.BaseActivity;
import com.thoughts.emotions.screens.login.LoginActivity;
import com.thoughts.emotions.utils.SharedPrefs;

public class SplashActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash2);
    getCompositionRoot();
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        if (!prefs.isUserLoggedIn()){
          startActivity(new Intent(SplashActivity.this, LoginActivity.class));
          finish();
        }else{
          Toast.makeText(SplashActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        }
      }
    }, 3000);
  }
}
