package com.thoughts.emotions.progressdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.thoughts.emotions.R;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ProgressDialogFragment extends DialogFragment {

  public ProgressDialogFragment() {
    // Required empty public constructor
  }

  public static ProgressDialogFragment newInstance() {
    return new ProgressDialogFragment();
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @NotNull ViewGroup container,
      @Nullable Bundle savedInstanceState
  ) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getDialog().getWindow())
        .setBackgroundDrawable(
            Objects.requireNonNull(getActivity())
                .getResources().getDrawable(android.R.color.transparent));
    setCancelable(false);
    return inflater.inflate(R.layout.fragment_progress_dialog, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    FragmentTransaction fragmentTransaction = manager.beginTransaction();
    fragmentTransaction.add(this, tag);
    fragmentTransaction.commitAllowingStateLoss();
  }
}
