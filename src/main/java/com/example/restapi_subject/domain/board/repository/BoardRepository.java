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
    void softDeleteById(Long id);
    void softDeleteByUserId(Long userId);
    void clear();
    List<Board> findAll();

    List<Board> findAllByCursor(Long cursorId, int size);
    Board findBoardWithDetailOrThrow(Long boardId);
    Board findByIdOrThrow(Long boardId);
    List<Board> findPage(Long cursorId, int limit);
    boolean existsById(Long id);
    void updateCommentCount(Long boardId, int delta);
    void updateViewCount(Long boardId, int delta);
    void updateLikeCount(Long boardId, int delta);

}