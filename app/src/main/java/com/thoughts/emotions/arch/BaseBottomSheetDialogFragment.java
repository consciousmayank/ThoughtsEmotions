package com.thoughts.emotions.arch;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.di.Module;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

  @LazyInit
  private Module compRoot;

  @LazyInit
  private Listener listener;

  protected Module getCompositionRoot() {
    if (compRoot == null) {
      compRoot = new Module(
          ((MyApp) requireActivity().getApplication()).getCompRoot(),
          (AppCompatActivity) requireActivity()
      );
    }
    return compRoot;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final BottomSheetDialog bottomSheetDialog =
        (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
    bottomSheetDialog.setOnShowListener(dialog -> {
      FrameLayout bottomSheet =
          bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
      BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
      behavior.setSkipCollapsed(true);
      if (isFullScreenSheet()) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      }
      behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
        @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
          if (listener != null) {
            listener.onSheetStateChanged(bottomSheet, newState);
          }
        }

        @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
      });
    });
    return bottomSheetDialog;
  }

  /** Return true here if you want to bottom sheet to be full screen on first show. */
  public abstract boolean isFullScreenSheet();

  public void setBottomSheetStateListener(Listener listener) {
    this.listener = listener;
  }

  public interface Listener {
    void onBottomSheetDialogDismissed();

    void onSheetStateChanged(@NonNull View bottomSheet, int newState);
  }
}