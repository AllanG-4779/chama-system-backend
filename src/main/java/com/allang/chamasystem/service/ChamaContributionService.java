package com.allang.chamasystem.service;

import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Chama;
import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.SessionStatus;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Slf4j
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
                .flatMap(chama -> shouldCreateSession(chama)
                        .flatMap(shouldCreate -> {
                            if (shouldCreate) {
                                return createNewSession(chama).then();
                            }
                            return Mono.empty();
                        }))
                .then();
    }

    private Mono<Boolean> shouldCreateSession(Chama chama) {
        LocalDate today = LocalDate.now();

        // Find the last session
        return contributionConfigRepository
                .findFirstByChamaIdOrderByEndDateDesc(chama.getId())
                .map(lastConfig -> {
                    // Check if we've passed the end date of the last session
                    // meaning it's time for a new session
                    return !lastConfig.getEndDate().isAfter(today);
                })
                .defaultIfEmpty(true); // No sessions exist, create first one
    }

    private Mono<ContributionConfig> createNewSession(Chama chama) {
        return contributionConfigRepository
                .findFirstByChamaIdOrderByEndDateDesc(chama.getId())
                .map(lastConfig -> {
                    // Next session starts where the last one ended
                    LocalDate nextStart = lastConfig.getEndDate();
                    return buildSession(chama, nextStart);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // First session starts at anchor date
                    LocalDate startDate = chama.getAnchorDate() != null
                            ? chama.getAnchorDate()
                            : LocalDate.now();
                    return Mono.just(buildSession(chama, startDate));
                }))
                .flatMap(contributionConfigRepository::save)
                .doOnSuccess(config -> {
                    assert config != null;
                    log.info("Created session for chama {}: {} to {}",
                            chama.getId(), config.getStartDate(), config.getEndDate());
                });
    }

    private ContributionConfig buildSession(Chama chama, LocalDate startDate) {
        ContributionConfig config = new ContributionConfig();
        config.setChamaId(chama.getId());
        config.setStartDate(startDate);

        LocalDate endDate = calculateEndDate(startDate, chama.getContributionSchedule());
        config.setEndDate(endDate);
        config.setGracePeriodEnd(endDate.plusDays(chama.getGracePeriodDays()));
//        config.setStatus("ACTIVE");
        config.setCreatedAt(LocalDateTime.now());

        return config;
    }

    private LocalDate calculateEndDate(LocalDate startDate, String schedule) {
        return switch (schedule) {
            case "DAILY" -> startDate.plusDays(1);
            case "WEEKLY" -> startDate.plusWeeks(1);
            case "BIWEEKLY" -> startDate.plusWeeks(2);
            case "MONTHLY" -> startDate.plusMonths(1);
            default -> throw new GenericExceptions("Unsupported schedule: " + schedule);
        };
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailySessions() {
        chamaRepository.findAllByActiveIsTrue()

                .flatMap(chama -> sessionService.createContributionSession(chama.getId())
                        .onErrorResume(error -> {
                            log.error("Failed to create session for chama {}", chama.getId(), error);
                            return Mono.empty();
                        }))
                .subscribe();
    }


}
