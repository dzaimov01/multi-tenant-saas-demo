package com.example.multitenant.application;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.stereotype.Service;

@Service
public class TenantMetricsService {
  private final Map<UUID, LongAdder> requestCounts = new ConcurrentHashMap<>();
  private final Map<UUID, LongAdder> activeUsers = new ConcurrentHashMap<>();

  public void recordRequest(UUID tenantId) {
    requestCounts.computeIfAbsent(tenantId, ignored -> new LongAdder()).increment();
  }

  public void recordActiveUser(UUID tenantId) {
    activeUsers.computeIfAbsent(tenantId, ignored -> new LongAdder()).increment();
  }

  public Map<UUID, Long> snapshotRequests() {
    return requestCounts.entrySet().stream()
        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().sum()));
  }

  public Map<UUID, Long> snapshotActiveUsers() {
    return activeUsers.entrySet().stream()
        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().sum()));
  }
}
