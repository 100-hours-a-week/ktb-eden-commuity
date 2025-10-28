package com.example.restapi_subject.global.common.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

public abstract class BaseInMemoryRepository<T> implements CrudCustomRepository<T, Long> {

    protected final Map<Long, T> store = new ConcurrentHashMap<>();
    protected final AtomicLong sequence = new AtomicLong(0);

    protected abstract Long getId(T entity);
    protected abstract T assignId(T entity, Long id);

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            long id = sequence.incrementAndGet();
            entity = assignId(entity, id);
        }
        store.put(getId(entity), entity);
        return entity;
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<T> update(Long id, UnaryOperator<T> updater) {
        T after = store.compute(id, (k, cur) ->
                cur == null ? null : updater.apply(cur)
        );
        return Optional.ofNullable(after);
    }

    @Override
    public List<T> findAll(){
        return store.values().stream()
                .sorted(Comparator.comparing(this::getId))
                .toList();
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
        sequence.set(0);
    }
}