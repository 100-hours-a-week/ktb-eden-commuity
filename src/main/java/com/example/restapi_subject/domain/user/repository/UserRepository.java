package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.global.common.repository.CrudCustomRepository;

import java.util.Optional;

public interface UserRepository extends CrudCustomRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}