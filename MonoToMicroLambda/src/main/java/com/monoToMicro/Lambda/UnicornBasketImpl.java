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
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import static software.amazon.lambda.powertools.utilities.EventDeserializer.extractDataFrom;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class UnicornBasketImpl implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
  private static final String UNICORN_TABLE_NAME = "unishop";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final DynamoDbAsyncClient ddb = DynamoDbAsyncClient.builder()
    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
    .httpClientBuilder(AwsCrtAsyncHttpClient.builder().maxConcurrency(50))
    .region(Region.of(System.getenv("AWS_REGION")))
    .overrideConfiguration(ClientOverrideConfiguration.builder()
          .addExecutionInterceptor(new TracingInterceptor()).build())
    .build();
  private static final DynamoDbEnhancedAsyncClient client = DynamoDbEnhancedAsyncClient.builder()
    .dynamoDbClient(ddb)
    .build();
  private static Map<String, String> staticHeaders = Map.of("Content-Type", "application/json",
    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "Access-Control-Allow-Origin", "*",
    "Access-Control-Allow-Methods", "DELETE,OPTIONS,POST,GET");

  static {
    try {
      final DynamoDbAsyncTable<UnicornBasket> unicornBasketTable = client.table(
        UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornBasket.class));

      unicornBasketTable.describeTable().get();
    } catch (DynamoDbException | ExecutionException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
      .withHeaders(staticHeaders).withBody("Unicorn Lives Matter").build();
  }

  public APIGatewayV2HTTPResponse addUnicornToBasket(APIGatewayV2HTTPEvent event, Context context)
      throws ExecutionException, InterruptedException {
    final DynamoDbAsyncTable<UnicornBasket> unicornBasketTable = client.table(
      UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornBasket.class));

    UnicornBasket unicornBasket = extractDataFrom(event).as(UnicornBasket.class);
    LambdaLogger logger = context.getLogger();
    logger.log("Incoming addUnicornToBasket request " + parseDTOToString(unicornBasket));

    //Get current basket
    UnicornBasket currentBasket = unicornBasketTable.getItem(r ->
      r.key(Key.builder().partitionValue(unicornBasket.getUuid()).build())).get();

    //if there is no current basket then use the incoming basket as the new basket
    if (currentBasket == null) {
      if (unicornBasket.getUuid() != null && unicornBasket.getUnicorns() != null) {
        unicornBasketTable.putItem(unicornBasket);
        Subsegment subsegment = AWSXRay.beginSubsegment("Creating new basket");
        subsegment.putMetadata("unicorns", "newBasket", unicornBasket.getUuid());
        AWSXRay.endSubsegment();
        return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
          .withHeaders(staticHeaders).withBody("Added Unicorn to basket").build();
      }
      return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
        .withHeaders(staticHeaders).withBody("No basket exist and none was created").build();
    }

    //basket already exist, will check if item exist and add if not found
    List<Unicorn> currentUnicorns = currentBasket.getUnicorns();
    List<Unicorn> unicornsToAdd = unicornBasket.getUnicorns();

    //Assuming only one will be added but checking for null or empty values
    if (unicornsToAdd != null && !unicornsToAdd.isEmpty()) {
      Unicorn unicornToAdd = unicornsToAdd.get(0);
      String unicornToAddUuid = unicornToAdd.getUuid();

      for (Unicorn currentUnicorn : currentUnicorns) {
        if (currentUnicorn.getUuid().equals(unicornToAddUuid)) {
          //The unicorn already exists, no need to add him.
          return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
            .withHeaders(staticHeaders).withBody("Unicorn already exists!").build();
        }
      }

      //Unicorn was not found, need to add and save
      currentUnicorns.add(unicornToAdd);
      currentBasket.setUnicorns(currentUnicorns);
      unicornBasketTable.putItem(currentBasket);
      Subsegment subsegment = AWSXRay.beginSubsegment("Adding new unicorn");
      subsegment.putMetadata("unicorns", "newUnicorn ", unicornToAdd.getUuid());
      AWSXRay.endSubsegment();
      return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
        .withHeaders(staticHeaders).withBody("Added Unicorn to basket").build();
    }
    return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
        .withHeaders(staticHeaders).withBody("Are you sure you added a Unicorn?").build();
  }

  public APIGatewayV2HTTPResponse removeUnicornFromBasket(APIGatewayV2HTTPEvent event, Context context)
    throws ExecutionException, InterruptedException {
    final DynamoDbAsyncTable<UnicornBasket> unicornBasketTable = client.table(
      UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornBasket.class));

    UnicornBasket unicornBasket = extractDataFrom(event).as(UnicornBasket.class);
    LambdaLogger logger = context.getLogger();
    logger.log("Incoming removeUnicornFromBasket request " + parseDTOToString(unicornBasket));

    //Get current basket
    UnicornBasket currentBasket = unicornBasketTable.getItem(r ->
      r.key(Key.builder().partitionValue(unicornBasket.getUuid()).build())).get();

    //if no basket exist then return an error
    if (currentBasket == null) {
      return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
        .withHeaders(staticHeaders).withBody("No basket exist, nothing to delete").build();
    }

    //basket exist, will check if item exist and will remove
    List<Unicorn> currentUnicorns = currentBasket.getUnicorns();
    List<Unicorn> unicornsToRemove = unicornBasket.getUnicorns();

    //Assuming only one will be removed but checking for null or empty values
    if (unicornsToRemove != null && !unicornsToRemove.isEmpty()) {
      Unicorn unicornToRemove = unicornsToRemove.get(0);
      String unicornToRemoveUuid = unicornToRemove.getUuid();

      for (Unicorn currentUnicorn : currentUnicorns) {
        if (currentUnicorn.getUuid().equals(unicornToRemoveUuid)) {
          currentUnicorns.remove(currentUnicorn);
          if (currentUnicorns.isEmpty()) {
            //no more unicorns in basket, will delete the basket
            unicornBasketTable.deleteItem(currentBasket);
            return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
              .withHeaders(staticHeaders).withBody("Unicorn was removed and basket was deleted!").build();
          } else {
            //keeping basket alive as more unicorns are in it
            currentBasket.setUnicorns(currentUnicorns);
            unicornBasketTable.putItem(currentBasket);
            return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
              .withHeaders(staticHeaders).withBody("Unicorn was removed! Other unicorns are still in basket").build();
          }
        }
      }

      if (currentBasket.getUnicorns() != null && currentBasket.getUnicorns().isEmpty()) {
        //no unicorn to remove, will try to remove the basket nonetheless
        unicornBasketTable.deleteItem(currentBasket);
      }
      return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
        .withHeaders(staticHeaders).withBody("Didn't find a unicorn to remove").build();
    }
    return APIGatewayV2HTTPResponse.builder().withStatusCode(200)
      .withHeaders(staticHeaders).withBody("Are you sure you asked to remove a Unicorn?").build();
  }

  public APIGatewayV2HTTPResponse getUnicornsBasket(APIGatewayV2HTTPEvent event, Context context)
    throws ExecutionException, InterruptedException {
    final DynamoDbAsyncTable<UnicornBasket> unicornBasketTable = client.table(
      UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornBasket.class));
    LambdaLogger logger = context.getLogger();

    UnicornBasket unicornBasket = null;
    logger.log("Incoming getUnicornsBasket request Path params" + event.getPathParameters().toString());
    String uuidPathParamValue = event.getPathParameters().get("uuid");

    if (StringUtils.isNotBlank(uuidPathParamValue)) {
      unicornBasket = unicornBasketTable
        .getItem(r -> r.key(Key.builder().partitionValue(uuidPathParamValue).build())).get();
    }
    return APIGatewayV2HTTPResponse.builder().withStatusCode(200).withHeaders(staticHeaders)
      .withBody(parseDTOToString(unicornBasket)).build();
  }

  private static String parseDTOToString(UnicornBasket unicornBasket) {
    try {
      return OBJECT_MAPPER.writeValueAsString(unicornBasket);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}