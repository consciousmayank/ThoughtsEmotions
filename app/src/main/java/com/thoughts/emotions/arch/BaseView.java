package com.thoughts.emotions.arch;

import android.content.Context;
import android.view.View;
import androidx.annotation.StringRes;
import butterknife.ButterKnife;

public abstract class BaseView implements ViewX {

  private View rootView;

  @Override
  public View getRootView() {
    return rootView;
  }

  protected void setRootView(View rootView) {
    this.rootView = rootView;
    ButterKnife.bind(this, rootView);
  }

  protected Context getContext() {
    return getRootView().getContext();
  }

  protected String getString(@StringRes int id) {
    return getContext().getString(id);
  }
}
