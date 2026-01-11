package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberOnboardingFacade {
    private final AppUserService appUserService;
    private final ChamaMemberService chamaMemberService;
    private final MemberService memberService;
    private final TransactionalOperator transactionalOperator;
    private final ChamaService chamaService;

    public Mono<ResponseDto> onboardMember(OnboardingRequest onboardingRequest) {
        return memberService.createMember(onboardingRequest.getMemberInfo())
                .flatMap(created -> {
                    var onboarding = onboardingRequest.getCredentials();
                    onboarding.setMemberId(created.id());
                    return chamaMemberService.addMemberToChama(created.id(), onboardingRequest.getChamaId())
                            .flatMap(onboardedMember -> {
                                var response = new ResponseDto("Member onboarded successfully",
                                        null, true, 201);
                                return Mono.just(response);
                            }).as(transactionalOperator::transactional);
                });
    }

    public Mono<ResponseDto> elevateMemberRole(Long chamaId, Long memberId, String newRole) {
        return chamaMemberService.updateChamaMemberRole(chamaId, memberId, newRole)
                .flatMap(updatedMember -> {
                    var response = new ResponseDto("Member role updated successfully",
                            updatedMember, true, 200);
                    return Mono.just(response);
                });
    }

    public Mono<ResponseDto> joinChamaAsMember(ChamaMemberDto chamaMemberDto) {
        return chamaMemberService.addMemberToChama(chamaMemberDto.getMemberId(), chamaMemberDto.getChamaId())
                .flatMap(joinedMember -> {
                    var response = new ResponseDto("Member joined chama successfully",
                            joinedMember, true, 201);
                    return Mono.just(response);
                });
    }


    public Mono<ResponseDto> onBoardChama(ChamaDto chamaDto) {
        return chamaService.createChama(chamaDto)
                .flatMap(createdChama -> memberService.createMember(chamaDto.contactPerson())
                        .flatMap(member -> chamaMemberService.updateChamaMemberRole(createdChama.getId(), member.id(), "ADMIN")))
                .flatMap(data -> {
                    var response = new ResponseDto("Chama onboarded successfully",
                            null, true, 201);
                    return Mono.just(response);
                });
    }

    public Mono<ResponseDto> activateLoginCredentials(AppUserDto appUserCreds) {
        return appUserService.createCredentials(appUserCreds)
                .flatMap(activatedUser -> {
                    var response = new ResponseDto("Login credentials activated successfully",
                            activatedUser, true, 200);
                    return Mono.just(response);
                });
    }



}
