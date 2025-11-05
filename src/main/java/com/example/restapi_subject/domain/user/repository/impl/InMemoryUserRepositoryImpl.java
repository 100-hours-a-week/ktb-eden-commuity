package com.example.restapi_subject.domain.user.repository.impl;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.common.repository.BaseInMemoryRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

@Slf4j
@Repository
@Profile("inmemory")
public class InMemoryUserRepositoryImpl extends BaseInMemoryRepository<User> implements UserRepository {

    private final Map<String, Long> emailIndex = new ConcurrentHashMap<>();
    private final Map<String, Long> nicknameIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User newUser) {
        User saved = super.save(newUser);
        updateIndexes(saved);
        return saved;
    }

    @Override
    public Optional<User> update(Long id, UnaryOperator<User> updater) {
        User before = findById(id)
                .orElseThrow(() ->new CustomException(ExceptionType.USER_NOT_FOUND));

        String oldEmailKey = norm(before.getEmail());
        String oldNicknameKey = norm(before.getNickname());

        Optional<User> updatedOpt = super.update(id, updater);
        updatedOpt.ifPresent(after -> {
            String newEmailKey = norm(after.getEmail());
            String newNickKey  = norm(after.getNickname());

            if (!Objects.equals(oldEmailKey, newEmailKey)) {
                if (oldEmailKey != null) emailIndex.remove(oldEmailKey);
                if (newEmailKey != null) emailIndex.put(newEmailKey, after.getId());
            }

            if (!Objects.equals(oldNicknameKey, newNickKey)) {
                if (oldNicknameKey != null) nicknameIndex.remove(oldNicknameKey);
                if (newNickKey != null) nicknameIndex.put(newNickKey, after.getId());
            }
        });
        return updatedOpt;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Long id = emailIndex.get(norm(email));
        if(id == null) return Optional.empty();
        return findById(id);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        Long id = nicknameIndex.get(nickname);
        if(id == null) return Optional.empty();
        return findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(norm(email));
    }

    @Override
    public boolean existsByNicknameAndDeletedFalse(String nickname) {
        Long id = nicknameIndex.get(nickname);
        if (id == null) return false;

        return findById(id)
                .map(user -> !user.isDeleted())
                .orElse(false);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return nicknameIndex.containsKey(nickname);
    }

    @Override
    public void delete(User user) {
        Long id = user.getId();
        findById(id).ifPresent(u -> {
            emailIndex.remove(norm(u.getEmail()));
            nicknameIndex.remove(norm(u.getNickname()));
        });
        super.delete(user);
    }

    @Override
    protected Long getId(User user) {
        return user.getId();
    }

    @Override
    protected User assignId(User user, Long id) {
        return user.withId(id);
    }

    private void updateIndexes(User user) {
        emailIndex.put(norm(user.getEmail()), user.getId());
        nicknameIndex.put(norm(user.getNickname()), user.getId());
    }

    private String norm(String email) {
        if(email == null) return null;
        return email.toLowerCase().trim();
    }
}
