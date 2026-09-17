package com.icklist.repository;

import com.icklist.model.Roast;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class RoastRepository {

    private static final String TABLE_NAME = "Roasts";
    private final DynamoDbClient dynamoDbClient;

    public RoastRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveAll(List<Roast> roasts) {
        if (roasts == null || roasts.isEmpty()) {
            return;
        }

        List<WriteRequest> writeRequests = new ArrayList<>();
        for (Roast r : roasts) {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("userId", AttributeValue.builder().s(r.getUserId()).build());
            item.put("roastId", AttributeValue.builder().s(r.getRoastId()).build());
            item.put("uploadBatchId", AttributeValue.builder().s(r.getUploadBatchId()).build());
            item.put("category", AttributeValue.builder().s(r.getCategory()).build());
            item.put("roastText", AttributeValue.builder().s(r.getRoastText()).build());
            item.put("severity", AttributeValue.builder().s(r.getSeverity()).build());
            item.put("emoji", AttributeValue.builder().s(r.getEmoji()).build());
            item.put("createdAt", AttributeValue.builder().s(r.getCreatedAt()).build());

            writeRequests.add(WriteRequest.builder()
                    .putRequest(PutRequest.builder().item(item).build())
                    .build());
        }

        BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
                .requestItems(Map.of(TABLE_NAME, writeRequests))
                .build();

        dynamoDbClient.batchWriteItem(batchRequest);
    }

    public List<Roast> findByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("userId = :userId")
                .scanIndexForward(false) // descending order
                .expressionAttributeValues(Map.of(
                        ":userId", AttributeValue.builder().s(userId).build()
                ))
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        List<Roast> result = new ArrayList<>();
        if (response.items() != null) {
            for (Map<String, AttributeValue> item : response.items()) {
                result.add(mapToRoast(item));
            }
        }
        // Sort by createdAt descending in memory as secondary guarantee
        result.sort((a, b) -> {
            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        return result;
    }

    private Roast mapToRoast(Map<String, AttributeValue> item) {
        String userId = item.get("userId") != null ? item.get("userId").s() : null;
        String roastId = item.get("roastId") != null ? item.get("roastId").s() : null;
        String batchId = item.get("uploadBatchId") != null ? item.get("uploadBatchId").s() : null;
        String category = item.get("category") != null ? item.get("category").s() : null;
        String roastText = item.get("roastText") != null ? item.get("roastText").s() : null;
        String severity = item.get("severity") != null ? item.get("severity").s() : "medium";
        String emoji = item.get("emoji") != null ? item.get("emoji").s() : "🔥";
        String createdAt = item.get("createdAt") != null ? item.get("createdAt").s() : null;

        return new Roast(userId, roastId, batchId, category, roastText, severity, emoji, createdAt);
    }
}
