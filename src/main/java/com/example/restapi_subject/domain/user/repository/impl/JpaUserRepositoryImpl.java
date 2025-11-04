package com.example.restapi_subject.domain.user.repository.impl;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.infra.UserEntity;
import com.example.restapi_subject.domain.user.repository.UserJpaRepository;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaUserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserEntity saved = userJpaRepository.save(UserEntity.from(user));
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserEntity::toDomain);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(UserEntity.from(user));
    }

    @Override
    public void clear() {
        userJpaRepository.deleteAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userJpaRepository.findByNickname(nickname)
                .map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
