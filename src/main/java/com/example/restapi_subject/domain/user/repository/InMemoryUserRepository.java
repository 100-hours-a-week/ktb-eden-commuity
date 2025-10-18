package com.example.restapi_subject.domain.user.repository;

import com.example.restapi_subject.domain.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Slf4j
@Repository
public class InMemoryUserRepository {
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public User save(User user) {
        if (user.getId() == null) user = user.withId(sequence.incrementAndGet());
        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    public Optional<User> findByNickname(String nickname) {
        return store.values().stream()
                .filter(user -> nickname.equals(user.getNickname()))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean existsByNickname(String nickname) {
        return findByNickname(nickname).isPresent();
    }

    public Optional<User> update(Long id, UnaryOperator<User> updater) {
        User after = store.compute(id, (k, cur) ->
                cur == null ? null : updater.apply(cur)
        );
        return Optional.ofNullable(after);
    }

    public void delete(Long id) {
        store.remove(id);
    }

    // 테스트용
    public void clear() {
        store.clear();
        sequence.set(0);
    }
}
