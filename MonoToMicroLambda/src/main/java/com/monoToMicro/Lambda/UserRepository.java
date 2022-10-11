package com.monoToMicro.Lambda;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class UserRepository {

  private static final String UNICORN_TABLE_NAME = "unishop-users";
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
      final DynamoDbAsyncTable<User> table = client.table(
        UNICORN_TABLE_NAME, TableSchema.fromBean(User.class));

      table.describeTable().get();
    } catch (DynamoDbException | ExecutionException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  private final DynamoDbAsyncTable<User> unicornProfileTable = client.table(
    UNICORN_TABLE_NAME, TableSchema.fromBean(User.class));


  public User getOrCreate(User user){
    //If we have no uuid, create a new profile and store it
    if(user.getUuid() == null){
      user.setUuid(UUID.randomUUID().toString());
      unicornProfileTable.putItem(user);
      return user;
    }

    //get the existing profile
    try {
      User currentUser = unicornProfileTable.getItem(r ->
        r.key(Key.builder().partitionValue(user.getUuid()).build())).get();

      if(currentUser == null){
        throw new IllegalArgumentException(String.format("Found no user with id %s", user.getUuid()));
      }

      return currentUser;
    }catch(ExecutionException | InterruptedException ex){
      throw new RuntimeException(ex);
    }
  }

  public User getByEmail(String email){
      AtomicReference<User> user = new AtomicReference<>();
      unicornProfileTable.scan(r ->
        r.filterExpression(
          Expression.builder().expression("email = :email").putExpressionValue(":email",
            AttributeValue.builder().s(email).build()).build()
        ).build()
      ).limit(1).subscribe(p -> {
        user.set(p.items().get(0));
      }).join();

      if(user.get() == null){
        throw new IllegalArgumentException(String.format("Found no user with email %s", email));
      }

      return user.get();
  }

  public User update(User profile){
    try {

      //Get current profile
      User currentProfile = unicornProfileTable.getItem(r ->
        r.key(Key.builder().partitionValue(profile.getUuid()).build())).get();

      //If there is no current profile then use the incoming profile as new profile
      if (currentProfile == null) {
        throw new IllegalArgumentException(String.format("Found no profile with id%s", profile.getUuid()));
      }

      //Profile already exist, lets update the values
      currentProfile.setFirstName(profile.getFirstName());
      currentProfile.setEmail(profile.getEmail());

      //Update the item in DynamoDB
      unicornProfileTable.updateItem(currentProfile);

      return profile;
    }catch (ExecutionException | InterruptedException ex){
      throw new RuntimeException(ex);
    }
  }
}
