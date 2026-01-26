package com.allang.chamasystem.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingWebFilter implements WebFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("=== INCOMING REQUEST ===");
        log.info("Method: {}", request.getMethod());
        log.info("URI: {}", request.getURI());
        log.info("Headers: {}", request.getHeaders());
        log.info("Content-Type: {}", request.getHeaders().getContentType());
        
        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                ServerHttpResponse response = exchange.getResponse();
                log.info("=== RESPONSE ===");
                log.info("Status: {}", response.getStatusCode());
                log.info("Headers: {}", response.getHeaders());
            })
            .doOnError(error -> {
                log.error("=== ERROR ===");
                log.error("Error processing request: {}", error.getMessage(), error);
            });
    }
}