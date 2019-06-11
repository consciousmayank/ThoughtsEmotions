package com.thoughts.emotions.arch;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.di.Module;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

  @LazyInit
  private Module compRoot;

  protected Module getCompositionRoot() {
    if (compRoot == null) {
      compRoot = new Module(
          ((MyApp) getApplication()).getCompRoot(),
          this
      );
    }
    return compRoot;
  }
}
