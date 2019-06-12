package com.thoughts.emotions.arch;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.di.Module;
import com.thoughts.emotions.utils.SharedPrefs;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

  @LazyInit
  private Module compRoot;

  @LazyInit
  public SharedPrefs prefs;

  protected Module getCompositionRoot() {
    if (compRoot == null) {
      compRoot = new Module(
          ((MyApp) getApplication()).getCompRoot(),
          this
      );
    }

    prefs = compRoot.getPreferences();

    return compRoot;
  }
}
