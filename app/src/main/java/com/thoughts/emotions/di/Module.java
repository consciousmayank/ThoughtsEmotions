package com.thoughts.emotions.di;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ViewSwitcher;
import androidx.appcompat.app.AppCompatActivity;

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

}
