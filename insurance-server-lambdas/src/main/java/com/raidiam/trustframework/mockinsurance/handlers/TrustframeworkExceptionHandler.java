package com.raidiam.trustframework.mockinsurance.handlers;

import com.raidiam.trustframework.mockinsurance.exceptions.TrustframeworkException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {TrustframeworkException.class, ExceptionHandler.class})
public class TrustframeworkExceptionHandler implements ExceptionHandler<TrustframeworkException, HttpResponse<Object>> {

  @Override
  public HttpResponse<Object> handle(HttpRequest request, TrustframeworkException ex) {
    return HttpResponse.serverError().status(400);
  }
}
