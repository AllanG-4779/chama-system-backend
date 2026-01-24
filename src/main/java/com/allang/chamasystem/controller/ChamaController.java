package com.allang.chamasystem.controller;

import com.allang.chamasystem.dto.AppUserDto;
import com.allang.chamasystem.dto.ContributionDto;
import com.allang.chamasystem.dto.ResponseDto;
import com.allang.chamasystem.service.ChamaContributionConfigService;
import com.allang.chamasystem.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ChamaController {
    private final ChamaContributionConfigService chamaContributionConfigService;
    private final ContributionService chamaContributionService;

    @PostMapping("/chamas/contribution/initialize/{chamaId}")
    public Mono<ResponseEntity<ResponseDto>> initializeChamaContribution(@PathVariable Long chamaId) {
        return chamaContributionConfigService.createContributionSession(chamaId)
                .map(responseDto -> {
                    var response = new ResponseDto("Contribution session initialized successfully",
                            responseDto, true, 201);
                    return ResponseEntity
                            .status(201)
                            .body(response);
                });
    }

    @PostMapping("/chama/contribute")
    public Mono<ResponseEntity<ResponseDto>> contributeToChama(@RequestBody @Valid ContributionDto contributionDto){
        return chamaContributionService.recordContributionPayment(contributionDto)
                .map(responseDto -> {
                    var response = new ResponseDto("Contribution recorded successfully",
                            responseDto, true, 201);
                    return ResponseEntity
                            .status(201)
                            .body(response);
                });
    }

}
