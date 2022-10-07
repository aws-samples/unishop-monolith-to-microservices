package com.monoToMicro.Lambda;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UnicornProfileRepository {

  public static final String PROFILE_ID_COOKIE = "unishopUserId";

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

  private final DynamoDbAsyncTable<UnicornProfile> unicornProfileTable = client.table(
    UNICORN_TABLE_NAME, TableSchema.fromBean(UnicornProfile.class));


  public UnicornProfile getOrCreate(String uuid){
    //If we have no uuid, create a new profile and store it
    if(uuid == null){
      UnicornProfile profile = new UnicornProfile(UUID.randomUUID().toString());
      unicornProfileTable.putItem(profile);
      return profile;
    }

    //get the existing profile
    try {
      UnicornProfile currentProfile = unicornProfileTable.getItem(r ->
        r.key(Key.builder().partitionValue(uuid).build())).get();

      if(currentProfile == null){
        throw new IllegalArgumentException(String.format("Found no profile with id%s", uuid));
      }

      return currentProfile;
    }catch(ExecutionException | InterruptedException ex){
      throw new RuntimeException(ex);
    }
  }

  public UnicornProfile update(UnicornProfile profile){
    try {

      //Get current profile
      UnicornProfile currentProfile = unicornProfileTable.getItem(r ->
        r.key(Key.builder().partitionValue(profile.getUuid()).build())).get();

      //If there is no current profile then use the incoming profile as new profile
      if (currentProfile == null) {
        throw new IllegalArgumentException(String.format("Found no profile with id%s", profile.getUuid()));
      }

      //Profile already exist, lets update the values
      currentProfile.setName(profile.getName());
      currentProfile.setEmail(profile.getEmail());

      //Update the item in DynamoDB
      unicornProfileTable.updateItem(currentProfile);

      return profile;
    }catch (ExecutionException | InterruptedException ex){
      throw new RuntimeException(ex);
    }
  }
}
