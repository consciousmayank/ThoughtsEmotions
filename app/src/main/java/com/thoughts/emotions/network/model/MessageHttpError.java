package com.thoughts.emotions.network.model;

public class MessageHttpError extends HttpError {
    private String responseCode;

    public String getResponseMessage() {
        if (this.responseMessage != null) {
            return responseMessage;
        } else {
            return "";
        }
    }

    public String getResponseCode() {
        return responseCode;
    }
}
