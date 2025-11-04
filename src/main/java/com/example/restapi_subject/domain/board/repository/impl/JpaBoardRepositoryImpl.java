package com.example.restapi_subject.domain.board.repository.impl;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.infra.BoardEntity;
import com.example.restapi_subject.domain.board.repository.BoardJpaRepository;
import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaBoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;

    @Override
    public Board save(Board board) {
        BoardEntity entity = BoardEntity.from(board, UserEntity.of(board.getAuthorId()));
        return boardJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Board> update(Long id, UnaryOperator<Board> updater) {
        return boardJpaRepository.findById(id)
                .map(BoardEntity::toDomain)
                .map(updater)
                .map(updated -> {
                    BoardEntity entity = BoardEntity.from(updated, UserEntity.of(updated.getAuthorId()));
                    return boardJpaRepository.save(entity).toDomain();
                });
    }

    @Override
    public Optional<Board> findById(Long id) {
        return boardJpaRepository.findById(id)
                .map(BoardEntity::toDomain);
    }

    @Override
    public void delete(Board board) {
        boardJpaRepository.delete(BoardEntity.of(board.getId()));
    }

    @Override
    public void clear() {
        boardJpaRepository.deleteAll();
    }

    @Override
    public List<Board> findAll() {
        return boardJpaRepository.findAll()
                .stream()
                .map(BoardEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Board> findAllByCursor(Long cursorId, int size) {
        return boardJpaRepository.findAllByCursor(cursorId, PageRequest.of(0, size))
                .stream()
                .map(BoardEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return boardJpaRepository.existsById(id);
    }
}
