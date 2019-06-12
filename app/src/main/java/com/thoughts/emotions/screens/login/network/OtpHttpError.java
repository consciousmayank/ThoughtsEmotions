package com.thoughts.emotions.screens.login.network;

import com.thoughts.emotions.network.model.HttpError;

public class OtpHttpError extends HttpError {

  public String getResponseMessage() {
    if (this.responseMessage != null) {
      return responseMessage;
    } else {
      return "";
    }
  }
}
