package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    void delete(User user);
    void clear();
    List<User> findAllByIdIn(Set<Long> ids);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNicknameAndDeletedFalse(String nickname);
}