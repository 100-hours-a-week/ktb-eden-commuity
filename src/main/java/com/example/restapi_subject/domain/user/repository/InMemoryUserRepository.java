package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.global.common.repository.BaseInMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository extends BaseInMemoryRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return store.values().stream()
                .filter(user -> nickname.equals(user.getNickname()))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return findByNickname(nickname).isPresent();
    }

    @Override
    protected Long getId(User user) {
        return user.getId();
    }

    @Override
    protected User assignId(User user, Long id) {
        return user.withId(id);
    }
}
