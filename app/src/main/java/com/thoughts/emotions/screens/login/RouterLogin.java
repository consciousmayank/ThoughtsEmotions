package com.thoughts.emotions.screens.login;

import android.app.Activity;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.thoughts.emotions.arch.NavigationIdContainer;

public class RouterLogin {

  private final Activity activity;
  //private final NavigationIdContainer navContainerId;

  //public RouterLogin(AppCompatActivity activity, NavigationIdContainer viewId) {
  public RouterLogin(AppCompatActivity activity) {
    this.activity = activity;
    //this.navContainerId = viewId;
  }

  public void toHomeScreen() {
    Toast.makeText(activity, "To HomeActivity", Toast.LENGTH_SHORT).show();
  }
}
