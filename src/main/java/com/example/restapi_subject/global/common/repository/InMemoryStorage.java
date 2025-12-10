package com.example.restapi_subject.global.common.repository;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

public class InMemoryStorage<T> {

    private final Map<Long, T> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);
    private final IdHandler<T> idHandler;


    public InMemoryStorage(IdHandler<T> idHander) {
        this.idHandler = idHander;
    }

    public T save(T entity) {
        Long id = idHandler.getId(entity);
        if (id == null) {
            id = sequence.incrementAndGet();
            entity = idHandler.assignId(entity, id);
        }
        store.put(id, entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(idHandler::getId))
                .toList();
    }

    public Optional<T> update(Long id, UnaryOperator<T> updater) {
        T updated = store.computeIfPresent(id, (k, cur) -> updater.apply(cur));
        return Optional.ofNullable(updated);
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public void clear() {
        store.clear();
        sequence.set(0);
    }

    public interface IdHandler<T> {
        Long getId(T entity);
        T assignId(T entity, Long id);
    }

}
