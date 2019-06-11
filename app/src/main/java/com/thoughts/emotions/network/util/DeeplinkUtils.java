package com.thoughts.emotions.network.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

public final class DeeplinkUtils {
  private static String deeplinkKey = "android-support-nav:controller:deepLinkIntent";

  private DeeplinkUtils(){

  }

  @Nullable
  public static Uri getUriFromDeeplinkBundle(@Nullable Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    Intent intent = (Intent) bundle.get(deeplinkKey);
    return intent != null ? intent.getData() : null;
  }
}
