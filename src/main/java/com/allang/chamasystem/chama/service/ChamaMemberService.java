package com.allang.chamasystem.chama.service;

import com.allang.chamasystem.dto.ChamaMemberDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.repository.ChamaMemberRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ChamaMemberService {
    private final ChamaMemberRepository chamaMemberRepository;

    public Mono<ChamaMemberDto> addMemberToChama(@NonNull ChamaMemberDto chamaMemberDto) {
        if (chamaMemberDto.getMemberId() == null || chamaMemberDto.getChamaId() == null) {
            return Mono.error(new GenericExceptions("Member ID and Chama ID must not be null"));
        }
        return chamaMemberRepository.existsByChamaIdAndMemberId(chamaMemberDto.getChamaId(), chamaMemberDto.getMemberId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new GenericExceptions("Member is already part of the Chama"));
                    } else {
                        var chamaMember = new com.allang.chamasystem.models.ChamaMember();
                        chamaMember.setChamaId(chamaMemberDto.getChamaId());
                        chamaMember.setMemberId(chamaMemberDto.getMemberId());
                        if (chamaMemberDto.getRole() != null) {
                            chamaMember.setRole(chamaMemberDto.getRole());
                        }
                        return chamaMemberRepository.save(chamaMember)
                                .map(savedChamaMember -> new ChamaMemberDto(
                                        savedChamaMember.getChamaId(),
                                        savedChamaMember.getMemberId(),
                                        savedChamaMember.getRole()
                                ));
                    }
                });
    }

}
