package com.allang.chamasystem.service;

import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

@Service
public class ChamaContributionService {

    private final ChamaRepository chamaRepository;
    private final ContributionConfigRepository contributionConfigRepository;

    public ChamaContributionService(ChamaRepository chamaRepository, ContributionConfigRepository contributionConfigRepository) {
        this.chamaRepository = chamaRepository;
        this.contributionConfigRepository = contributionConfigRepository;
    }

    public Mono<Void> createContributionSession(Long chamaId) {
        return chamaRepository.findById(chamaId)
                .switchIfEmpty(Mono.error(new GenericExceptions("Chama not found")))
                .flatMap(chama -> {
                    var frequency = chama.getContributionSchedule();
                    switch (frequency) {
                        case "WEEKLY" -> {
                            // Logic for weekly contribution session
                            return Mono.empty();
                        }
                        case "MONTHLY" -> {
                            // Logic for monthly contribution session
                            return Mono.empty();
                        }
                        case "DAILY" -> {
                            // Logic for quarterly contribution session
                            return Mono.empty();
                        }
                        default -> {
                            return Mono.error(new GenericExceptions("Invalid contribution frequency"));
                        }

                    });
                });


    }

    private ContributionConfig determineContributionConfig(String frequency){
        var currentYearMonth = YearMonth.now();
        var currentWeek = java.time.LocalDate.now().get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);


    }
}
