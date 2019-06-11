package com.thoughts.emotions.arch;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import com.thoughts.emotions.MyApp;
import com.thoughts.emotions.R;
import com.thoughts.emotions.di.Module;

public class BaseDialogFragment extends AppCompatDialogFragment {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    return dialog;
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.base_dialog_fragment, container, false);
  }

  @Override
  public void onStart() {
    super.onStart();

    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  @LazyInit
  private Module compRoot;

  protected Module getCompositionRoot() {
    if (compRoot == null) {
      compRoot =
          new Module(
              ((MyApp) requireActivity().getApplication()).getCompRoot(),
              (AppCompatActivity) requireActivity());
    }
    return compRoot;
  }
}
