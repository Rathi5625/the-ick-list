package com.icklist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icklist.dto.GenerateRoastResponse;
import com.icklist.dto.RoastDto;
import com.icklist.model.Roast;
import com.icklist.model.Transaction;
import com.icklist.repository.RoastRepository;
import com.icklist.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class RoastService {

    private static final Logger logger = LoggerFactory.getLogger(RoastService.class);

    private final TransactionRepository transactionRepository;
    private final RoastRepository roastRepository;
    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final BedrockPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    private final boolean bedrockEnabled;
    private final String modelId;

    public RoastService(
            TransactionRepository transactionRepository,
            RoastRepository roastRepository,
            BedrockRuntimeClient bedrockRuntimeClient,
            BedrockPromptBuilder promptBuilder,
            ObjectMapper objectMapper,
            @Value("${aws.bedrock.enabled:false}") boolean bedrockEnabled,
            @Value("${aws.bedrock.model-id:amazon.nova-lite-v1:0}") String modelId
    ) {
        this.transactionRepository = transactionRepository;
        this.roastRepository = roastRepository;
        this.bedrockRuntimeClient = bedrockRuntimeClient;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
        this.bedrockEnabled = bedrockEnabled;
        this.modelId = modelId;
    }

    public GenerateRoastResponse generateRoasts(String uploadBatchId, String userId) {
        List<Transaction> transactions = transactionRepository.findByUploadBatchId(userId, uploadBatchId);

        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("No transactions found for batch ID: " + uploadBatchId);
        }

        // Edge case: too few transactions
        if (transactions.size() < 3) {
            return generateLowDataRoast(userId, uploadBatchId);
        }

        List<RoastDto> roastDtos;

        if (bedrockEnabled) {
            logger.info("[BEDROCK] Invoking real Amazon Bedrock model ({}) for batch {}...", modelId, uploadBatchId);
            roastDtos = callBedrock(transactions);
        } else {
            logger.info("[BEDROCK] (STUBBED) Using mock roast response — real Bedrock call skipped");
            roastDtos = getStubbedRoasts();
        }

        // Assign IDs, timestamps, and save to DynamoDB
        String now = Instant.now().toString();
        List<Roast> roastsToSave = new ArrayList<>();

        for (RoastDto dto : roastDtos) {
            if (dto.getRoastId() == null || dto.getRoastId().isEmpty()) {
                dto.setRoastId(UUID.randomUUID().toString());
            }
            dto.setUploadBatchId(uploadBatchId);
            dto.setCreatedAt(now);
            roastsToSave.add(new Roast(
                    userId,
                    dto.getRoastId(),
                    uploadBatchId,
                    dto.getCategory(),
                    dto.getRoastText(),
                    dto.getSeverity(),
                    dto.getEmoji(),
                    now
            ));
        }

        roastRepository.saveAll(roastsToSave);

        return new GenerateRoastResponse(roastDtos);
    }

    private List<RoastDto> callBedrock(List<Transaction> transactions) {
        try {
            String prompt = promptBuilder.buildPrompt(transactions);

            // Amazon Nova JSON request payload
            Map<String, Object> requestPayload = Map.of(
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "inferenceConfig", Map.of(
                            "temperature", 0.7,
                            "max_new_tokens", 1000
                    )
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestPayload);

            InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBodyJson))
                    .build();

            InvokeModelResponse response = bedrockRuntimeClient.invokeModel(invokeRequest);
            String responseJson = response.body().asUtf8String();

            return parseBedrockResponse(responseJson);

        } catch (Exception e) {
            logger.error("[BEDROCK] Error calling Bedrock: {}. Falling back to high-quality roasts.", e.getMessage(), e);
            return getStubbedRoasts();
        }
    }

    private List<RoastDto> parseBedrockResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // Nova format: output.message.content[0].text
            String textContent = null;
            if (root.has("output") && root.get("output").has("message")) {
                JsonNode contentArray = root.get("output").get("message").get("content");
                if (contentArray != null && contentArray.isArray() && !contentArray.isEmpty()) {
                    textContent = contentArray.get(0).get("text").asText();
                }
            } else if (root.has("completion")) {
                textContent = root.get("completion").asText();
            }

            if (textContent == null) {
                textContent = rawResponse;
            }

            // Extract JSON block if wrapped in markdown code fence
            String cleanJson = textContent.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            } else if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substring(3);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }
            cleanJson = cleanJson.trim();

            JsonNode parsed = objectMapper.readTree(cleanJson);
            JsonNode roastsArray = parsed.has("roasts") ? parsed.get("roasts") : parsed;

            List<RoastDto> list = new ArrayList<>();
            if (roastsArray != null && roastsArray.isArray()) {
                for (JsonNode node : roastsArray) {
                    String category = node.has("category") ? node.get("category").asText() : "impulse_category_spikes";
                    String roastText = node.has("roastText") ? node.get("roastText").asText() : "";
                    String severity = node.has("severity") ? node.get("severity").asText() : "medium";
                    String emoji = node.has("emoji") ? node.get("emoji").asText() : "🔥";

                    list.add(new RoastDto(UUID.randomUUID().toString(), category, roastText, severity, emoji));
                }
            }

            return list.isEmpty() ? getStubbedRoasts() : list;

        } catch (Exception e) {
            logger.warn("[BEDROCK] Could not parse Bedrock output into JSON: {}. Using fallback roasts.", e.getMessage());
            return getStubbedRoasts();
        }
    }

    private GenerateRoastResponse generateLowDataRoast(String userId, String uploadBatchId) {
        String now = Instant.now().toString();
        RoastDto lowDataRoast = new RoastDto(
                UUID.randomUUID().toString(),
                uploadBatchId,
                "impulse_category_spikes",
                "Your statement has like three transactions. Did you borrow someone else's card to look responsible, or are you hiding the real receipts?",
                "mild",
                "👀",
                now
        );
        Roast roast = new Roast(
                userId,
                lowDataRoast.getRoastId(),
                uploadBatchId,
                lowDataRoast.getCategory(),
                lowDataRoast.getRoastText(),
                lowDataRoast.getSeverity(),
                lowDataRoast.getEmoji(),
                now
        );
        roastRepository.saveAll(List.of(roast));
        return new GenerateRoastResponse(List.of(lowDataRoast));
    }

    /**
     * Hand-written realistic stubbed roasts crafted from sample-transactions.csv.
     * Hits 4 of the 5 fixed categories in the exact "brutally honest, funny friend" tone.
     */
    public List<RoastDto> getStubbedRoasts() {
        return List.of(
                new RoastDto(
                        UUID.randomUUID().toString(),
                        "late_night_spending",
                        "Ordering $38 of Taco Bell at 11:45 PM followed by a 2 AM Gopuff haul is not hunger, bestie. That is a full-blown emotional spiral.",
                        "medium",
                        "🌙"
                ),
                new RoastDto(
                        UUID.randomUUID().toString(),
                        "duplicate_purchases",
                        "You bought the Elden Ring DLC twice in eight minutes. The first boss didn't even touch you yet and your bank account already took a critical hit.",
                        "unserious",
                        "👯"
                ),
                new RoastDto(
                        UUID.randomUUID().toString(),
                        "subscription_creep",
                        "Paying $40 for Equinox and $10 for Duolingo while exercising zero times and speaking zero French is pure subscription philanthropy.",
                        "mild",
                        "💳"
                ),
                new RoastDto(
                        UUID.randomUUID().toString(),
                        "weekend_overspending",
                        "You survived on $13 salads Monday through Thursday like a Victorian orphan, only to blow $565 between Friday night and Saturday brunch. Make it make sense.",
                        "unserious",
                        "🍸"
                )
        );
    }
}
