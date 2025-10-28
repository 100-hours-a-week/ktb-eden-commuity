package com.example.restapi_subject.domain.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(Long userId, String refreshToken);
    Optional<String> get(Long userId);
    boolean compareAndSet(Long userId, String expected, String newValue);
    boolean compareAndDelete(Long userId, String expected);
    void delete(Long userId);

}
