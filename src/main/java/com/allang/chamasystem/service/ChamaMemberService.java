package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.ChamaMemberDto;
import com.allang.chamasystem.dto.ResponseDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.ChamaMember;
import com.allang.chamasystem.repository.ChamaMemberRepository;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import com.allang.chamasystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamaMemberService {
    private final ChamaMemberRepository chamaMemberRepository;
    private final ChamaRepository chamaRepository;
    private final ContributionConfigRepository contributionConfigRepository;
    private final MemberRepository memberRepository;
    private final InvoiceService invoiceService;
    private final PenaltyService penaltyService;

    public Mono<ChamaMemberDto> addMemberToChama(Long memberId, Long chamaId) {
        if (memberId == null || chamaId == null) {
            return Mono.error(new GenericExceptions("Member ID and Chama ID must not be null"));
        }
        return chamaRepository.existsById(chamaId)
                .flatMap(chamaExists -> {
                    if (!chamaExists) {
                        return Mono.error(new GenericExceptions("Chama with ID " + chamaId + " does not exist"));
                    }
                    return chamaMemberRepository.existsByChamaIdAndMemberId(chamaId, memberId)
                            .zipWith(memberRepository.existsById(memberId))
                            .flatMap(exists -> {
                                if (!exists.getT2()) {
                                    return Mono.error(new GenericExceptions("Member with ID " + memberId + " does not exist"));
                                }
                                if (exists.getT1()) {
                                    return Mono.error(new GenericExceptions("Member is already part of the Chama"));
                                } else {
                                    var chamaMember = new com.allang.chamasystem.models.ChamaMember();
                                    chamaMember.setChamaId(chamaId);
                                    chamaMember.setMemberId(memberId);
                                    chamaMember.setRole("MEMBER");
                                    return chamaMemberRepository.save(chamaMember)
                                            .flatMap(savedMember ->
                                                contributionConfigRepository.findAllByChamaId(savedMember.getChamaId())
                                                        .next() // Take only the first config to avoid duplicate invoices
                                                        .flatMap(config -> invoiceService.autoCreateInvoicesForMember(savedMember.getId(), config.getId()))

                                                        .doOnSuccess(v -> log.info("Created invoices for new member {} in chama {}", savedMember.getMemberId(), savedMember.getChamaId()))
                                                        .doOnError(error -> log.error("Failed to create invoices for new member: {}", error.getMessage(), error))
                                                        .onErrorResume(e -> Mono.empty())
                                                        .thenReturn(savedMember)
                                            )
                                            .map(savedChamaMember -> new ChamaMemberDto(
                                                    savedChamaMember.getMemberId(),
                                                    savedChamaMember.getChamaId(),
                                                    savedChamaMember.getRole()
                                            ));
                                }
                            });
                });
    }

    public Mono<ResponseDto> removeMemberFromChama(ChamaMemberDto chamaMemberDto) {
        if (chamaMemberDto.getMemberId() == null || chamaMemberDto.getChamaId() == null) {
            return Mono.error(new GenericExceptions("Member ID and Chama ID must not be null"));
        }
        return chamaMemberRepository.findByChamaIdAndMemberId(chamaMemberDto.getChamaId(), chamaMemberDto.getMemberId())
                .switchIfEmpty(Mono.error(new GenericExceptions("No details could be found in the system for the provided Chama ID and Member ID")))
                .flatMap(chamaMember -> {
                    chamaMember.setDeletedAt(LocalDateTime.now());
                    return chamaMemberRepository.save(chamaMember)
                            .map(deletedChamaMember -> new ResponseDto("Member removed from Chama successfully",
                                    null, true, 200));
                });
    }

    public Mono<ChamaMember> updateChamaMemberRole(Long chamaId, Long memberId, String role) {
        return chamaMemberRepository.findByChamaIdAndMemberId(chamaId, memberId)
                .switchIfEmpty(Mono.error(new GenericExceptions("No details could be found in the system for the provided Chama ID and Member ID")))
                .flatMap(chamaMember -> {
                    // Update role logic here
                    // For example, let's say we set the role to "ADMIN"
                    chamaMember.setRole(role);
                    return chamaMemberRepository.save(chamaMember);
                });

    }


}
