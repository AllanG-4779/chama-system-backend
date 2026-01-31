package com.allang.chamasystem.service;

import com.allang.chamasystem.events.ContributionPeriodCreatedEvent;
import com.allang.chamasystem.events.bus.SystemEventBus;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Chama;
import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Slf4j
@Service
public class ChamaContributionConfigService {

    // Create new session X days before current period ends (advance notice for members)
    private static final int ADVANCE_NOTICE_DAYS = 7;

    private final ChamaRepository chamaRepository;
    private final ContributionConfigRepository contributionConfigRepository;
    private final SystemEventBus systemEventBus;

    public ChamaContributionConfigService(ChamaRepository chamaRepository, ContributionConfigRepository contributionConfigRepository, SystemEventBus systemEventBus) {
        this.chamaRepository = chamaRepository;
        this.contributionConfigRepository = contributionConfigRepository;
        this.systemEventBus = systemEventBus;
    }

    public Mono<ContributionConfig> createContributionSession(Long chamaId) {
        return chamaRepository.findById(chamaId)
                .switchIfEmpty(Mono.error(new GenericExceptions("Chama not found")))
                .flatMap(chama -> shouldCreateSession(chama)
                        .flatMap(shouldCreate -> {
                            if (shouldCreate) {
                                return createNewSession(chama);
                            }
                            return Mono.error(new GenericExceptions("No new session needed at this time"));
                        }));

    }

    private Mono<Boolean> shouldCreateSession(Chama chama) {
        LocalDate today = LocalDate.now();

        // Find the last session
        return contributionConfigRepository
                .findFirstByChamaIdOrderByEndDateDesc(chama.getId())
                .map(lastConfig -> {
                    // Create new session ADVANCE_NOTICE_DAYS before current period ends
                    // This gives members advance notice of upcoming contributions
                    LocalDate createThreshold = lastConfig.getEndDate().minusDays(ADVANCE_NOTICE_DAYS);
                    return !today.isBefore(createThreshold); // today >= threshold
                })
                .defaultIfEmpty(true); // No sessions exist, create first one
    }

    private Mono<ContributionConfig> createNewSession(Chama chama) {
        return contributionConfigRepository
                .findFirstByChamaIdOrderByEndDateDesc(chama.getId())
                .map(lastConfig -> {
                    // Next session starts where the last one ended plus one day
                    LocalDate nextStart = lastConfig.getEndDate().plusDays(1);
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
                    systemEventBus.publishContributionPeriodCreated(new ContributionPeriodCreatedEvent(chama.getId(), config.getId(), false, null));
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
        var provisionalGracePeriodEnd = startDate.plusDays(chama.getGracePeriodDays());
        config.setGracePeriodEnd(provisionalGracePeriodEnd.isBefore(endDate) ? provisionalGracePeriodEnd : endDate);
//        config.setStatus("ACTIVE");
        config.setPeriod(calculatePeriod(startDate, chama.getContributionSchedule()));
        config.setAmount(chama.getContributionAmount());
        config.setFrequency(chama.getContributionSchedule());
        config.setCreatedAt(LocalDateTime.now());
        return config;
    }

    private LocalDate calculateEndDate(LocalDate startDate, String schedule) {

        return switch (schedule) {

            case "DAILY" -> startDate;

            case "WEEKLY" -> startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            case "BIWEEKLY" -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int week = startDate.get(weekFields.weekOfWeekBasedYear());

                // If odd week → end this week, if even → end next week
                boolean isOddWeek = week % 2 != 0;
                LocalDate endOfWeek = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                yield isOddWeek
                        ? endOfWeek
                        : endOfWeek.plusWeeks(1);
            }

            case "MONTHLY" -> startDate.with(TemporalAdjusters.lastDayOfMonth());

            default -> throw new GenericExceptions("Unsupported schedule: " + schedule);
        };
    }

    private String calculatePeriod(LocalDate date, String schedule) {

        return switch (schedule) {

            case "DAILY" -> date.format(DateTimeFormatter.ISO_LOCAL_DATE); // 2026-01-24

            case "WEEKLY", "BIWEEKLY" -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int week = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.get(weekFields.weekBasedYear());
                yield String.format("%d-W%02d", year, week); // 2026-W04
            }

            case "MONTHLY" -> date.format(DateTimeFormatter.ofPattern("yyyy-MM")); // 2026-01

            case "YEARLY" -> String.valueOf(date.getYear()); // 2026

            default -> throw new GenericExceptions("Unsupported schedule: " + schedule);
        };
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailySessions() {
        chamaRepository.findAllByActiveIsTrue()
                .flatMap(chama -> createContributionSession(chama.getId())
                        .onErrorResume(error -> {
                            log.error("Failed to create session for chama {}", chama.getId(), error);
                            return Mono.empty();
                        }))
                .subscribe();
    }


}
