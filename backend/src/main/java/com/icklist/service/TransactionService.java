package com.icklist.service;

import com.icklist.dto.UploadResponse;
import com.icklist.model.Transaction;
import com.icklist.repository.TransactionRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public UploadResponse uploadTransactions(MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded CSV file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must be a CSV (.csv)");
        }

        List<Transaction> transactions = new ArrayList<>();
        String uploadBatchId = UUID.randomUUID().toString();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            if (rows == null || rows.isEmpty()) {
                throw new IllegalArgumentException("CSV file contains no data");
            }

            // Parse header row
            String[] headers = rows.get(0);
            Map<String, Integer> colMap = mapHeaders(headers);

            validateRequiredColumns(colMap);

            int dateIdx = colMap.get("date");
            int amountIdx = colMap.get("amount");
            int merchantIdx = colMap.get("merchant");
            int categoryIdx = colMap.get("category");

            // Parse data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row == null || row.length == 0 || isBlankRow(row)) {
                    continue; // skip blank lines
                }

                int rowNum = i + 1; // 1-based row number in CSV file
                int maxIdx = Math.max(Math.max(dateIdx, amountIdx), Math.max(merchantIdx, categoryIdx));
                if (row.length <= maxIdx) {
                    throw new IllegalArgumentException("Row " + rowNum + " has missing columns");
                }

                String date = row[dateIdx].trim();
                String rawAmount = row[amountIdx].trim();
                String merchant = row[merchantIdx].trim();
                String category = row[categoryIdx].trim();

                if (date.isEmpty()) {
                    throw new IllegalArgumentException("Date cannot be empty on row " + rowNum);
                }
                if (merchant.isEmpty()) {
                    throw new IllegalArgumentException("Merchant cannot be empty on row " + rowNum);
                }
                if (category.isEmpty()) {
                    throw new IllegalArgumentException("Category cannot be empty on row " + rowNum);
                }

                Double amount = parseAmount(rawAmount, rowNum);

                String transactionId = UUID.randomUUID().toString();
                transactions.add(new Transaction(userId, transactionId, date, amount, merchant, category, uploadBatchId));
            }

            if (transactions.isEmpty()) {
                throw new IllegalArgumentException("CSV contains no valid transaction rows");
            }

            // Save all transactions to DynamoDB
            transactionRepository.saveAll(transactions);

            return new UploadResponse(uploadBatchId, transactions.size());

        } catch (IOException | CsvException e) {
            throw new IllegalArgumentException("Failed to read CSV file: " + e.getMessage());
        }
    }

    private Map<String, Integer> mapHeaders(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i] != null) {
                String normalized = headers[i].trim().toLowerCase();
                map.put(normalized, i);
            }
        }
        return map;
    }

    private void validateRequiredColumns(Map<String, Integer> colMap) {
        List<String> missing = new ArrayList<>();
        if (!colMap.containsKey("date")) missing.add("Date");
        if (!colMap.containsKey("amount")) missing.add("Amount");
        if (!colMap.containsKey("merchant")) missing.add("Merchant");
        if (!colMap.containsKey("category")) missing.add("Category");

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("CSV missing required column(s): " + String.join(", ", missing) +
                    ". Required columns: Date, Amount, Merchant, Category");
        }
    }

    private Double parseAmount(String rawAmount, int rowNum) {
        // Strip common currency symbols, commas, quotes
        String cleaned = rawAmount.replace("$", "").replace("€", "").replace("£", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format on row " + rowNum + ": '" + rawAmount + "'");
        }
    }

    private boolean isBlankRow(String[] row) {
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
