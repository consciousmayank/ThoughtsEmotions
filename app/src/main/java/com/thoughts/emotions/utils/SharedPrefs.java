package com.thoughts.emotions.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefs {

  public static final String IS_USER_LOGGED_IN = "is_user_logged_in";

  SharedPreferences sharedPreferences;

  public SharedPrefs(Context context) {
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  private SharedPreferences.Editor getEditor(){
    return sharedPreferences.edit();
  }

  public boolean isUserLoggedIn() {
    return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
  }

  public void setIsUserLoggedIn(boolean value){
    getEditor().putBoolean(IS_USER_LOGGED_IN, value).commit();

  }
}
