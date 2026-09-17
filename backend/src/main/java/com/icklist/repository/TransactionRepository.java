package com.icklist.repository;

import com.icklist.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class TransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(TransactionRepository.class);
    private static final String TABLE_NAME = "Transactions";
    private static final int BATCH_WRITE_LIMIT = 25; // DynamoDB maximum BatchWriteItem chunk size

    private final DynamoDbClient dynamoDbClient;

    public TransactionRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveAll(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        // Chunk into groups of 25 (DynamoDB limit)
        for (int i = 0; i < transactions.size(); i += BATCH_WRITE_LIMIT) {
            List<Transaction> chunk = transactions.subList(i, Math.min(i + BATCH_WRITE_LIMIT, transactions.size()));
            List<WriteRequest> writeRequests = new ArrayList<>();

            for (Transaction tx : chunk) {
                Map<String, AttributeValue> item = new HashMap<>();
                item.put("userId", AttributeValue.builder().s(tx.getUserId()).build());
                item.put("transactionId", AttributeValue.builder().s(tx.getTransactionId()).build());
                item.put("date", AttributeValue.builder().s(tx.getDate()).build());
                item.put("amount", AttributeValue.builder().n(String.valueOf(tx.getAmount())).build());
                item.put("merchant", AttributeValue.builder().s(tx.getMerchant()).build());
                item.put("category", AttributeValue.builder().s(tx.getCategory()).build());
                item.put("uploadBatchId", AttributeValue.builder().s(tx.getUploadBatchId()).build());

                writeRequests.add(WriteRequest.builder()
                        .putRequest(PutRequest.builder().item(item).build())
                        .build());
            }

            Map<String, List<WriteRequest>> requestItems = new HashMap<>();
            requestItems.put(TABLE_NAME, writeRequests);

            BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
                    .requestItems(requestItems)
                    .build();

            BatchWriteItemResponse response = dynamoDbClient.batchWriteItem(batchRequest);

            // Handle unprocessed items with simple backoff if needed
            int retryCount = 0;
            Map<String, List<WriteRequest>> unprocessed = response.unprocessedItems();
            while (unprocessed != null && !unprocessed.isEmpty() && retryCount < 3) {
                logger.warn("Retrying {} unprocessed items in batch write...", unprocessed.size());
                try {
                    Thread.sleep(100 * (retryCount + 1));
                } catch (InterruptedException ignored) {}
                BatchWriteItemResponse retryResponse = dynamoDbClient.batchWriteItem(
                        BatchWriteItemRequest.builder().requestItems(unprocessed).build()
                );
                unprocessed = retryResponse.unprocessedItems();
                retryCount++;
            }
        }
    }

    public List<Transaction> findByUploadBatchId(String userId, String uploadBatchId) {
        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("userId = :userId")
                .filterExpression("uploadBatchId = :batchId")
                .expressionAttributeValues(Map.of(
                        ":userId", AttributeValue.builder().s(userId).build(),
                        ":batchId", AttributeValue.builder().s(uploadBatchId).build()
                ))
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        List<Transaction> result = new ArrayList<>();
        if (response.items() != null) {
            for (Map<String, AttributeValue> item : response.items()) {
                result.add(mapToTransaction(item));
            }
        }
        return result;
    }

    private Transaction mapToTransaction(Map<String, AttributeValue> item) {
        String userId = item.get("userId") != null ? item.get("userId").s() : null;
        String txId = item.get("transactionId") != null ? item.get("transactionId").s() : null;
        String date = item.get("date") != null ? item.get("date").s() : null;
        Double amount = item.get("amount") != null ? Double.parseDouble(item.get("amount").n()) : 0.0;
        String merchant = item.get("merchant") != null ? item.get("merchant").s() : null;
        String category = item.get("category") != null ? item.get("category").s() : null;
        String batchId = item.get("uploadBatchId") != null ? item.get("uploadBatchId").s() : null;

        return new Transaction(userId, txId, date, amount, merchant, category, batchId);
    }
}
