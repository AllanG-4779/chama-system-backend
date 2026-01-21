package com.allang.chamasystem.service;

import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Chama;
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

    private ContributionConfig determineContributionConfig(String frequency, Chama chama) {
        return contributionConfigRepository.findByChamaIdOrderByEndDateDesc(chama.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    var config = new ContributionConfig();
                    config.setChamaId(chama.getId());
                    config.setStartDate(chama.getAnchorDate());
                    return Mono.just(config);
                })).flatMap(config -> {
                    switch (frequency) {
                        case "WEEKLY" -> {
                            config.setEndDate(config.getStartDate().plusWeeks(1));
                            return Mono.just(config);
                        }
                        case "MONTHLY" -> {
                            config.setEndDate(config.getStartDate().plusMonths(1));
                            return Mono.just(config);
                        }
                        case "DAILY" -> {
                            config.setEndDate(config.getStartDate().plusDays(1));
                            return Mono.just(config);
                        }
                        default -> {
                            return Mono.error(new GenericExceptions("Invalid contribution frequency"));
                        }
                        config.setGracePeriodEnd(config.getStartDate().plusDays(chama.getGracePeriodDays()));
                        return Mono.just(config);
                    }

                });


    }
}
