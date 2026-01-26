package com.allang.chamasystem.events.subscribers;

import com.allang.chamasystem.events.bus.SystemEventBus;
import com.allang.chamasystem.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ContributionCreatedListener {

    private final SystemEventBus eventBus;
    private final InvoiceService invoiceService;
    private Disposable subscription;

    public ContributionCreatedListener(SystemEventBus eventBus, InvoiceService invoiceService) {
        this.eventBus = eventBus;
        this.invoiceService = invoiceService;
    }

    @PostConstruct
    //TODO: Check why publishing event on join chama not working for this listener
    public void initialize() {
        subscription = eventBus.contributionPeriodEvents()

                .doOnNext(event -> log.info("Received contribution period event: {}", event))
                .flatMap(contributionEvent ->
                        {
                            Mono<Void> checked;
                            if (!contributionEvent.singleMember()) {
                                checked = invoiceService.autoCreateInvoicesForContributions(
                                        contributionEvent.chamaId(),
                                        contributionEvent.periodId()
                                );
                            } else {
                                checked = invoiceService.autoCreateInvoicesForMember(
                                        contributionEvent.memberId(),
                                        contributionEvent.periodId()
                                );
                            }
                            return checked
                                    .doOnSuccess(result ->
                                            log.info("Successfully created invoices for period: {}", contributionEvent.periodId())
                                    )
                                    .doOnError(error ->
                                            log.error("Failed to create invoices for period: {}, error: {}",
                                                    contributionEvent.periodId(), error.getMessage(), error)
                                    )
                                    .onErrorResume(error -> {
                                        // Don't break the stream on error
                                        log.error("Recovering from error, continuing to process events");
                                        return Mono.empty();
                                    });
                        }
                )
                .subscribe();
        log.info("ContributionCreatedListener initialized and subscribed to events");
    }

    @PreDestroy
    public void cleanup() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            log.info("ContributionCreatedListener subscription disposed");
        }
    }
}