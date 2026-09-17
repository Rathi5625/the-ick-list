package com.icklist.controller;

import com.icklist.dto.GenerateRoastRequest;
import com.icklist.dto.GenerateRoastResponse;
import com.icklist.dto.RoastDto;
import com.icklist.model.Roast;
import com.icklist.repository.RoastRepository;
import com.icklist.service.RoastService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roasts")
public class RoastController {

    private final RoastService roastService;
    private final RoastRepository roastRepository;

    public RoastController(RoastService roastService, RoastRepository roastRepository) {
        this.roastService = roastService;
        this.roastRepository = roastRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerateRoastResponse> generate(
            @Valid @RequestBody GenerateRoastRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        GenerateRoastResponse response = roastService.generateRoasts(request.getUploadBatchId(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<GenerateRoastResponse> getHistory(Authentication authentication) {
        String userId = authentication.getName();
        List<Roast> roasts = roastRepository.findByUserId(userId);
        List<RoastDto> dtos = roasts.stream()
                .map(r -> new RoastDto(
                        r.getRoastId(),
                        r.getUploadBatchId(),
                        r.getCategory(),
                        r.getRoastText(),
                        r.getSeverity(),
                        r.getEmoji(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new GenerateRoastResponse(dtos));
    }
}
