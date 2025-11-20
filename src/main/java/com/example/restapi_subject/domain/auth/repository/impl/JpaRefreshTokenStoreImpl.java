package com.example.restapi_subject.domain.auth.repository.impl;


import com.example.restapi_subject.domain.auth.infra.RefreshTokenEntity;
import com.example.restapi_subject.domain.auth.repository.RefreshTokenJpaRepository;
import com.example.restapi_subject.domain.auth.repository.RefreshTokenRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaRefreshTokenStoreImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public void save(Long userId, String refreshToken) {
        RefreshTokenEntity token = jpaRepository.findByUserId(userId)
                .map(existing -> {
                    existing.update(refreshToken);
                    return existing;
                })
                .orElseGet(() -> RefreshTokenEntity.of(userId, refreshToken));
        jpaRepository.save(token);
    }

    @Override
    public Optional<String> get(Long userId) {
        return jpaRepository.findByUserId(userId)
                .map(RefreshTokenEntity::getToken);
    }

    @Override
    public boolean compareAndSet(Long userId, String expected, String newValue) {
        RefreshTokenEntity token = jpaRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.TOKEN_NOT_FOUND));

        if (!token.getToken().equals(expected)) return false;

        token.update(newValue);
        jpaRepository.save(token);
        return true;
    }

    @Override
    public boolean compareAndDelete(Long userId, String expected) {
        RefreshTokenEntity token = jpaRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.TOKEN_NOT_FOUND));

        if (!token.getToken().equals(expected)) return false;
        jpaRepository.delete(token);
        return true;
    }

    @Override
    public void delete(Long userId) {
        if (!jpaRepository.existsByUserId(userId)) {
            throw new CustomException(ExceptionType.TOKEN_NOT_FOUND);
        }
        jpaRepository.deleteByUserId(userId);
    }
}
