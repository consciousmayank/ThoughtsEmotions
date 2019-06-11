package com.thoughts.emotions.network.model;

import retrofit2.Response;

public class InvalidModelResponse extends RuntimeException {

  static final long serialVersionUID = -1;

  public Response response;

  public <T> InvalidModelResponse(Response<T> response) {
    this.response = response;
  }
}
