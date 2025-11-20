package com.example.restapi_subject.domain.auth.repository;

import com.example.restapi_subject.domain.auth.infra.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
