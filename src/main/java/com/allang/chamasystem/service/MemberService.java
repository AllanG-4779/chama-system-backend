package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.MemberDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Member;
import com.allang.chamasystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;


    public Mono<MemberDto> createMember(MemberDto memberDto) {
        return Mono.zip(
                        memberRepository.existsByIdNumber(memberDto.idNumber()),
                        memberRepository.existsByEmail(memberDto.email()),
                        memberRepository.existsByPhoneNumber(memberDto.phoneNumber())
                )
                .flatMap(tuple -> {
                    if (tuple.getT1()) {
                        return Mono.error(new GenericExceptions("Member with ID number "
                                + memberDto.idNumber() + " already exists"));
                    }
                    if (tuple.getT2()) {
                        return Mono.error(new GenericExceptions("Member with email "
                                + memberDto.email() + " already exists"));
                    }
                    if (tuple.getT3()) {
                        return Mono.error(new GenericExceptions("Member with phone number "
                                + memberDto.phoneNumber() + " already exists"));
                    }

                    Member member = getMember(memberDto);
                    return memberRepository.save(member)
                            .map(this::mapToDto);
                });
    }

    public Mono<MemberDto> getMemberById(Long id) {
        return memberRepository.findById(id)
                .switchIfEmpty(Mono.error(new GenericExceptions("Member with ID " + id + " not found")))
                .map(this::mapToDto);
    }


    private static Member getMember(MemberDto memberDto) {
        var member = new Member();
        member.setFirstName(memberDto.firstName());
        member.setLastName(memberDto.lastName());
        member.setIdNumber(memberDto.idNumber());
        member.setEmail(memberDto.email());
        member.setPhoneNumber(memberDto.phoneNumber());
        member.setDateOfBirth(memberDto.dateOfBirth());
        member.setStatus("ACTIVE");
        member.setIdNumber(memberDto.idNumber());
        return member;
    }

    private MemberDto mapToDto(Member member) {
        return new MemberDto(null,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getEmail(),
                member.getDateOfBirth(),
                member.getIdNumber()

        );
    }

}
