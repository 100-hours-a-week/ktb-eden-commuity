package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    void delete(User user);
    void clear();

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNicknameAndDeletedFalse(String nickname);
}