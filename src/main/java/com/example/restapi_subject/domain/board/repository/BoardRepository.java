package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.domain.Board;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface BoardRepository {

    Board save(Board board);
    Optional<Board> update(Long id, UnaryOperator<Board> updater);
    Optional<Board> findById(Long id);
    void delete(Board board);
    void clear();
    List<Board> findAll();

    List<Board> findAllByCursor(Long cursorId, int size);

    boolean existsById(Long id);

}