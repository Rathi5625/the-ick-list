package com.icklist.controller;

import com.icklist.dto.UploadResponse;
import com.icklist.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        // authentication.getName() is the userId set by JwtAuthFilter
        String userId = authentication.getName();
        UploadResponse response = transactionService.uploadTransactions(file, userId);
        return ResponseEntity.ok(response);
    }
}
