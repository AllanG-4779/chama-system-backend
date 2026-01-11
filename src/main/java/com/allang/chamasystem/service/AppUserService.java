package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.AppUserDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.AppUser;
import com.allang.chamasystem.repository.AppUserRepository;
import com.allang.chamasystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AppUserService {
    private final MemberRepository memberRepository;
    private final AppUserRepository appUserRepository;

    public Mono<AppUserDto> createCredentials(AppUserDto appUserDto) {
        var newCustomer = new AppUser();
        return memberRepository.findByIdNumber(appUserDto.getIdNumber())
                .switchIfEmpty(Mono.error(new GenericExceptions("No member found with the provided ID number")))
                .flatMap(check -> appUserRepository.existsByMemberId(check.getId())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new GenericExceptions("Credentials already exist for this member"));
                            }
                            return Mono.just(check);
                        }))
                .flatMap(memberDto -> appUserRepository.existsByUsername(appUserDto.getUsername())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new GenericExceptions("Username already exists"));
                            }
                            newCustomer.setUsername(appUserDto.getUsername());
                            newCustomer.setPasswordHash(appUserDto.getPassword());
                            newCustomer.setActive(false);
                            newCustomer.setRoles(appUserDto.getRoles());
                            newCustomer.setMemberId(memberDto.getId());
                            return appUserRepository.save(newCustomer)
                                    .map(savedUser -> {
                                        appUserDto.setUsername(savedUser.getUsername());
                                        return appUserDto;
                                    });
                        }));

    }


}
