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
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.lambda.powertools.utilities.JsonConfig;

import static software.amazon.lambda.powertools.utilities.EventDeserializer.extractDataFrom;

import java.util.Map;

public class CreateHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private final UserRepository repository = new UserRepository();

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context)  {
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    try{
      User userRequest = extractDataFrom(event).as(User.class);
      User profile = repository.getOrCreate(userRequest);
      response
        .withHeaders(Map.of("Content-Type", "application/json",
          "Access-Control-Allow-Headers", "Content-Type, x-requested-with",
          "Access-Control-Allow-Origin", "*",
          "Access-Control-Allow-Methods", "OPTIONS,POST,GET"))
        .withBody(JsonConfig.get().getObjectMapper().writeValueAsString(profile))
        .withStatusCode(200);
    }catch (Exception exception){
      exception.printStackTrace();
      response
        .withBody("An error occurred while fetching the user")
        .withStatusCode(400);
    }
    return response;
  }
}