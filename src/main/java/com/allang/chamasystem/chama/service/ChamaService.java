package com.allang.chamasystem.chama.service;

import com.allang.chamasystem.dto.ChamaDto;
import com.allang.chamasystem.dto.ResponseDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Chama;
import com.allang.chamasystem.repository.ChamaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ChamaService {

    private final ChamaRepository chamaRepository;

    public Mono<ResponseDto> createChama(ChamaDto chamaDto) {
        return chamaRepository.existsByRegistrationNumber(chamaDto.registrationNumber())
                .flatMap(existingChama -> {
                    if (existingChama) {
                        return Mono.error(new GenericExceptions("Chama with registration number "
                                + chamaDto.registrationNumber() + " already exists."));
                    } else {
                        var chama = new Chama();
                        chama.setName(chamaDto.name());
                        chama.setRegistrationNumber(chamaDto.registrationNumber());
                        chama.setDescription(chamaDto.description());
                        chama.setContributionAmount(chamaDto.contributionAmount());
                        chama.setContributionSchedule(chamaDto.contributionSchedule());
                        return chamaRepository.save(chama)
                                .flatMap(savedChama -> Mono.just(new ResponseDto("Chama created successfully",
                                        savedChama, true, 200)));
                    }
                });
    }


    public Flux<ResponseDto> getAllChamas(int page, int size) {
        var pagination = PageRequest.of(Math.max(page, 0), Math.max(size, 10));
        return chamaRepository.findAllBy(pagination)
                .map(each -> new ResponseDto("Chama fetched successfully", mapToDto(each), true, 200));
    }

    public Mono<ResponseDto> getChamaById(Long id, String registrationNumber) {
        if (id != null) {
            return chamaRepository.findById(id)
                    .flatMap(chama -> Mono.just(new ResponseDto("Chama fetched successfully", mapToDto(chama), true, 200)));
        } else {
            return chamaRepository.findByRegistrationNumber(registrationNumber)
                    .flatMap(optionalChama -> {
                        if (optionalChama.isEmpty()) {
                            return Mono.error(new GenericExceptions("Chama with registration number "
                                    + registrationNumber + " not found."));
                        } else {
                            var chama = optionalChama.get();
                            return Mono.just(new ResponseDto("Chama fetched successfully", mapToDto(chama), true, 200));
                        }
                    });
        }
    }


    public Mono<ResponseDto> updateChama(Long id, ChamaDto chamaDto) {
        return chamaRepository.findById(id)
                .flatMap(existingChama -> {
                    if (chamaDto.name() != null) {
                        existingChama.setName(chamaDto.name());
                    }
                    if (chamaDto.description() != null) {
                        existingChama.setDescription(chamaDto.description());
                    }
                    if (chamaDto.contributionSchedule() != null) {
                        existingChama.setContributionSchedule(chamaDto.contributionSchedule());
                    }
                    if (chamaDto.contributionAmount() != null) {
                        existingChama.setContributionAmount(chamaDto.contributionAmount());
                    }
                    return chamaRepository.save(existingChama)
                            .flatMap(updatedChama -> Mono.just(new ResponseDto("Chama updated successfully",
                                    mapToDto(updatedChama), true, 200)));
                })
                .switchIfEmpty(Mono.error(new GenericExceptions("Chama with ID " + id + " not found.")));
    }

    public Mono<ResponseDto> deleteChama(Long id) {
        return chamaRepository.findById(id)
                .flatMap(existingChama ->
                        {
                            existingChama.setStatus("DISSOLVED");
                            return chamaRepository.save(existingChama)
                                    .flatMap(updatedChama -> Mono.just(new ResponseDto("Chama dissolved successfully",
                                            mapToDto(updatedChama), true, 200)));
                        }
                )
                .switchIfEmpty(Mono.error(new GenericExceptions("Chama with ID " + id + " not found.")));
    }


    private ChamaDto mapToDto(Chama chama) {
        return new ChamaDto(
                chama.getId(),
                chama.getName(),
                chama.getDescription(),
                chama.getContributionAmount(),
                chama.getContributionSchedule(),
                chama.getRegistrationNumber()
        );
    }


}
