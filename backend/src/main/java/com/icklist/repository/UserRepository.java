package com.icklist.repository;

import com.icklist.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String TABLE_NAME = "Users";
    private static final String EMAIL_INDEX = "email-index";

    private final DynamoDbClient dynamoDbClient;

    public UserRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(User user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.builder().s(user.getUserId()).build());
        item.put("email", AttributeValue.builder().s(user.getEmail().toLowerCase().trim()).build());
        item.put("passwordHash", AttributeValue.builder().s(user.getPasswordHash()).build());
        if (user.getName() != null) {
            item.put("name", AttributeValue.builder().s(user.getName()).build());
        }
        if (user.getAvatarInitials() != null) {
            item.put("avatarInitials", AttributeValue.builder().s(user.getAvatarInitials()).build());
        }
        if (user.getAvatarColor() != null) {
            item.put("avatarColor", AttributeValue.builder().s(user.getAvatarColor()).build());
        }
        item.put("createdAt", AttributeValue.builder().s(user.getCreatedAt()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        String normalizedEmail = email.toLowerCase().trim();

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .indexName(EMAIL_INDEX)
                .keyConditionExpression("email = :email")
                .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(normalizedEmail).build()))
                .limit(1)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        if (response.items() == null || response.items().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToUser(response.items().get(0)));
    }

    public Optional<User> findById(String userId) {
        if (userId == null) {
            return Optional.empty();
        }

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("userId", AttributeValue.builder().s(userId).build()))
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        if (!response.hasItem() || response.item() == null || response.item().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToUser(response.item()));
    }

    private User mapToUser(Map<String, AttributeValue> item) {
        String userId = item.get("userId") != null ? item.get("userId").s() : null;
        String email = item.get("email") != null ? item.get("email").s() : null;
        String passwordHash = item.get("passwordHash") != null ? item.get("passwordHash").s() : null;
        String name = item.get("name") != null ? item.get("name").s() : null;
        String avatarInitials = item.get("avatarInitials") != null ? item.get("avatarInitials").s() : null;
        String avatarColor = item.get("avatarColor") != null ? item.get("avatarColor").s() : null;
        String createdAt = item.get("createdAt") != null ? item.get("createdAt").s() : null;

        return new User(userId, email, passwordHash, name, avatarInitials, avatarColor, createdAt);
    }
}
