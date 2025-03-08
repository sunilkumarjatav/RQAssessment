package com.reliaquest.server.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.random.RandomGenerator;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RandomRequestLimitInterceptor implements HandlerInterceptor {

    private static final int REQUEST_LIMIT = RandomGenerator.getDefault().nextInt(5, 10);
    private static final Duration REQUEST_BACKOFF_DURATION =
            Duration.ofSeconds(RandomGenerator.getDefault().nextInt(30, 90));

    private final AtomicReference<RequestLimit> requestLimit = new AtomicReference<>(RequestLimit.init());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (requestLimit.get().getCount() >= REQUEST_LIMIT) {
            if (Instant.now()
                    .minus(REQUEST_BACKOFF_DURATION)
                    .isBefore(requestLimit.get().getLastRequested())) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
            if (Instant.now()
                    .minus(REQUEST_BACKOFF_DURATION)
                    .isAfter(requestLimit.get().getLastRequested())) {
                requestLimit.set(RequestLimit.init());
            }
        } else {
            requestLimit.getAndUpdate(
                    currentRequestLimit -> new RequestLimit(currentRequestLimit.count() + 1, Instant.now()));
        }

        return true;
    }

    private record RequestLimit(@Getter int count, @Getter Instant lastRequested) {
        public static RequestLimit init() {
            return new RequestLimit(0, Instant.now());
        }
    }
}
