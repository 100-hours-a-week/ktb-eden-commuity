package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.global.common.repository.CrudCustomRepository;

import java.util.Optional;

public interface UserRepository extends CrudCustomRepository<User, Long> {

    public boolean existsByNickname(String nickname);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByNickname(String nickname);
    public boolean existsByEmail(String email);
}