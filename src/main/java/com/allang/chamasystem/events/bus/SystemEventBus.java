package com.allang.chamasystem.events.bus;

import com.allang.chamasystem.events.ContributionPeriodCreatedEvent;
import com.allang.chamasystem.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class SystemEventBus {
    // Creating a pipeline for user created events, this literally means, that the pipeline will
    // publish more than one item, and several subscribers can listen to the same event,
    // and onBackpressureBuffer means that if the subscribers are slow, the events will be buffered to cope
    // with the speed difference
    private final Sinks.Many<UserCreatedEvent> sink =  Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<ContributionPeriodCreatedEvent> periodSink =  Sinks.many().multicast().onBackpressureBuffer();


    public void publishUserCreated(UserCreatedEvent event) {
        sink.tryEmitNext(event);
    }

    public Flux<UserCreatedEvent> userEvents() {
        return sink.asFlux();
    }
    public void publishContributionPeriodCreated(ContributionPeriodCreatedEvent event) {
        periodSink.tryEmitNext(event);
    }

    public Flux<ContributionPeriodCreatedEvent> contributionPeriodEvents() {
        return periodSink.asFlux();
    }



}
