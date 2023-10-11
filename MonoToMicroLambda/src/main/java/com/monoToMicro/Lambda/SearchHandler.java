/**
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.monoToMicro.Lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.awssdk.utils.StringUtils;
import software.amazon.lambda.powertools.utilities.JsonConfig;

import java.util.Map;

public class SearchHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private final UserRepository repository = new UserRepository();

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
    LambdaLogger logger = context.getLogger();
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    // if there's no path param, return not found
    if (null == event || null == event.getPathParameters()) {
      response
        .withBody("no parameter found")
        .withStatusCode(400);
      return response;
    }

    logger.log("Incoming searchUser request Path params" + event.getPathParameters().toString());

    // if there's no email path param, return parameter not found
    String emailPathParamValue = event.getPathParameters().get("email");
    if (StringUtils.isBlank(emailPathParamValue)) {
      response
        .withBody("no email Parameter found")
        .withStatusCode(400);
      return response;
    }

    User profile = repository.getByEmail(emailPathParamValue);
    try {
      response
        .withHeaders(Map.of("Content-Type", "application/json",
          "Access-Control-Allow-Headers", "Content-Type, x-requested-with",
          "Access-Control-Allow-Origin", "*",
          "Access-Control-Allow-Methods", "DELETE,OPTIONS,POST,GET"))
        .withBody(JsonConfig.get().getObjectMapper().writeValueAsString(profile))
        .withStatusCode(200);
    } catch (Exception exception) {
      logger.log(exception.getMessage());
      response
        .withBody(exception.getMessage())
        .withStatusCode(400);
    }
    return response;
  }
}