package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.AppUserDto;
import com.allang.chamasystem.events.UserCreatedEvent;
import com.allang.chamasystem.events.bus.SystemEventBus;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.AppUser;
import com.allang.chamasystem.repository.AppUserRepository;
import com.allang.chamasystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class AppUserService {
    private final MemberRepository memberRepository;
    private final AppUserRepository appUserRepository;
    private final SystemEventBus userEventBus;

    public Mono<AppUserDto> createCredentials(AppUserDto appUserDto) {
        var newCustomer = new AppUser();
        return appUserRepository.existsByMemberId(appUserDto.getMemberId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new GenericExceptions("Credentials already exist for this member"));
                    }
                    return Mono.just(appUserDto);
                })
                .flatMap(memberDto -> appUserRepository.existsByUsername(appUserDto.getUsername())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new GenericExceptions("Username already exists"));
                            }
                            newCustomer.setUsername(appUserDto.getUsername());
                            newCustomer.setPasswordHash(appUserDto.getPassword());
                            newCustomer.setActive(false);
                            newCustomer.setRoles(appUserDto.getRoles());
                            newCustomer.setMemberId(memberDto.getMemberId());
                            return appUserRepository.save(newCustomer)
                                    .map(savedUser -> {
                                        userEventBus.publishUserCreated(new UserCreatedEvent(savedUser.getUsername(), Instant.now()));
                                        appUserDto.setUsername(savedUser.getUsername());
                                        return appUserDto;
                                    });
                        }));

    }


}
