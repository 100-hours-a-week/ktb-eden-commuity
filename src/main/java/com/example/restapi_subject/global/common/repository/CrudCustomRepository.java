package com.example.restapi_subject.global.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface CrudCustomRepository<T, ID> {
    T save(T t);
    Optional<T> findById(ID id);
    List<T> findAll();
    Optional<T> update(Long id, UnaryOperator<T> updater);
    void delete(ID id);
    void clear();
    boolean existsById(ID id);
}