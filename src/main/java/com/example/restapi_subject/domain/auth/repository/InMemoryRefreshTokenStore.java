package com.example.restapi_subject.domain.auth.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRefreshTokenStore implements RefreshTokenRepository {

    private final Map<Long, String> latestRtByUser = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, String refreshToken) {
        latestRtByUser.put(userId, refreshToken);
    }

    @Override
    public Optional<String> get(Long userId) {
        return Optional.ofNullable(latestRtByUser.get(userId));
    }

    @Override
    public boolean compareAndSet(Long userId, String expected, String newValue) {
        return latestRtByUser.replace(userId, expected, newValue);
    }

    @Override
    public boolean compareAndDelete(Long userId, String expected) {
        return latestRtByUser.remove(userId, expected);
    }

    @Override
    public void delete(Long userId) {
        latestRtByUser.remove(userId);
    }
}
