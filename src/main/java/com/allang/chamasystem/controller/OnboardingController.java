package com.allang.chamasystem.controller;

import com.allang.chamasystem.dto.*;
import com.allang.chamasystem.service.ChamaContributionConfigService;
import com.allang.chamasystem.service.ChamaService;
import com.allang.chamasystem.service.MemberOnboardingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OnboardingController {
    private final MemberOnboardingFacade onboardingFacade;
    private final ChamaService chamaService;
    private final ChamaContributionConfigService contributionService;

    @PostMapping("/chamas")
    public Mono<ResponseEntity<ResponseDto>> createChama(@RequestBody ChamaDto chamaDto) {
        return onboardingFacade.onBoardChama(chamaDto)
                .map(responseDto -> ResponseEntity
                        .status(201)
                        .body(responseDto));

    }

    @GetMapping("/chamas/{id}")

    public Mono<ResponseEntity<ResponseDto>> getChamaById(@PathVariable() Long id) {
        return chamaService.getChamaById(id, String.valueOf(id))
                .map(responseDto -> ResponseEntity
                        .ok()
                        .body(responseDto));
    }

    @PostMapping("/members")
    public Mono<ResponseEntity<ResponseDto>> createMember(@RequestBody OnboardingRequest onboardingRequest) {
        return onboardingFacade.onboardMember(onboardingRequest)
                .map(responseDto -> ResponseEntity
                        .status(201)
                        .body(responseDto));
    }
    @PostMapping("/chamas/join")
    public Mono<ResponseEntity<ResponseDto>> joinChama(@RequestBody ChamaMemberDto chamaMemberDto) {
        return onboardingFacade.joinChamaAsMember(chamaMemberDto)
                .flatMap(item -> Mono.just(ResponseEntity
                        .ok()
                        .body(item)));
    }

}
