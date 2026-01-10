package com.allang.chamasystem.repository;

import com.allang.chamasystem.chama.service.ChamaMemberService;
import com.allang.chamasystem.dto.ChamaMemberDto;
import com.allang.chamasystem.models.ChamaMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ChamaMemberRepository extends ReactiveCrudRepository<ChamaMember, Long> {
    Mono<Boolean> existsByChamaIdAndMemberId(Long chamaId, Long memberId);
}
