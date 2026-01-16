package com.example.multitenant.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {
  private final int limitPerMinute;
  private final Map<String, Counter> counters = new ConcurrentHashMap<>();

  public LoginRateLimiter(@Value("${app.rate-limit.login-per-minute:10}") int limitPerMinute) {
    this.limitPerMinute = limitPerMinute;
  }

  public boolean tryConsume(String key) {
    Counter counter = counters.computeIfAbsent(key, ignored -> new Counter());
    return counter.tryConsume(limitPerMinute);
  }

  private static class Counter {
    private Instant windowStart = Instant.now();
    private int count = 0;

    synchronized boolean tryConsume(int limit) {
      Instant now = Instant.now();
      if (now.isAfter(windowStart.plusSeconds(60))) {
        windowStart = now;
        count = 0;
      }
      if (count >= limit) {
        return false;
      }
      count++;
      return true;
    }
  }
}
