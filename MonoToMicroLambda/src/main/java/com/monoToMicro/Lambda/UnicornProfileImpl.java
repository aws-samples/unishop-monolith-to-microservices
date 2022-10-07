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
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UnicornProfileImpl implements RequestHandler<UnicornProfile, String> {
  private static final String UNICORN_TABLE_NAME = "unishop-profile";
  private static final DynamoDbAsyncClient ddb = DynamoDbAsyncClient.builder()
    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
    .httpClientBuilder(AwsCrtAsyncHttpClient.builder().maxConcurrency(50))
    .region(Region.of(System.getenv("AWS_REGION")))
    .build();
  private static final DynamoDbEnhancedAsyncClient client = DynamoDbEnhancedAsyncClient.builder()
    .dynamoDbClient(ddb)
    .build();

  static {
    try {
      final DynamoDbAsyncTable<UnicornProfile> table = client.table(
        UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornProfile.class));

      table.describeTable().get();
    } catch (DynamoDbException | ExecutionException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public String handleRequest(UnicornProfile profile, Context context)  {
    try {
      final DynamoDbAsyncTable<UnicornProfile> unicornProfileTable = client.table(
        UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornProfile.class));

      //Get current profile
      UnicornProfile currentProfile = unicornProfileTable.getItem(r ->
        r.key(Key.builder().partitionValue(profile.getUuid()).build())).get();

      //If there is no current profile then use the incoming profile as new profile
      if (currentProfile == null) {
        if (profile.getUuid() != null) {
          unicornProfileTable.putItem(profile);
          return "Created new profile";
        }
        return "Missing uuid";
      }

      //Profile already exist, lets update the values
      currentProfile.setName(profile.getName());
      currentProfile.setEmail(profile.getEmail());

      //Update the item in DynamoDB
      unicornProfileTable.updateItem(currentProfile);

      return "Profile updated!";
    }catch (ExecutionException | InterruptedException ex){
      throw new RuntimeException(ex);
    }
  }
}