package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.ChamaMemberDto;
import com.allang.chamasystem.dto.ResponseDto;
import com.allang.chamasystem.events.ContributionPeriodCreatedEvent;
import com.allang.chamasystem.events.bus.SystemEventBus;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.ChamaMember;
import com.allang.chamasystem.repository.ChamaMemberRepository;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
    private final SystemEventBus systemEventBus;

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
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new GenericExceptions("Member is already part of the Chama"));
                                } else {
                                    var chamaMember = new com.allang.chamasystem.models.ChamaMember();
                                    chamaMember.setChamaId(chamaId);
                                    chamaMember.setMemberId(memberId);
                                    chamaMember.setRole("MEMBER");
                                    return chamaMemberRepository.save(chamaMember)
                                            .flatMapMany(published -> contributionConfigRepository.findAllByChamaId(chamaMember.getChamaId())
                                                    .flatMap(each -> publishMemberJoinedEvent(published, each.getId()).thenReturn(published)))
                                            .collectList().then(Mono.just(chamaMember))
                                            .map(savedChamaMember -> new ChamaMemberDto(
                                                    savedChamaMember.getChamaId(),
                                                    savedChamaMember.getMemberId(),
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

    private Mono<Void> publishMemberJoinedEvent(ChamaMember chamaMember, Long periodId) {
        return Mono.fromRunnable(() -> {
            try {
                // Publish event for new member joining
                systemEventBus.publishContributionPeriodCreated(
                        new ContributionPeriodCreatedEvent(
                                chamaMember.getChamaId(),
                                periodId,
                                true,
                                chamaMember.getMemberId()
                        )
                );
                log.info("Published MemberJoinedEvent for member {} in chama {}",
                        chamaMember.getMemberId(), chamaMember.getChamaId());
            } catch (Exception e) {
                log.error("Failed to publish MemberJoinedEvent: {}", e.getMessage(), e);
                // Don't fail the whole operation if event publishing fails
            }
        }).then();
    }

}
