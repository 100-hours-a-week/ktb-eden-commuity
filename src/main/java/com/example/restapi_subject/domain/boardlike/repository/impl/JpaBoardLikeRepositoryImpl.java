package com.example.restapi_subject.domain.boardlike.repository.impl;

import com.example.restapi_subject.domain.boardlike.infra.BoardLikeEntity;
import com.example.restapi_subject.domain.boardlike.repository.BoardLikeJpaRepository;
import com.example.restapi_subject.domain.boardlike.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaBoardLikeRepositoryImpl implements BoardLikeRepository {

    private final BoardLikeJpaRepository boardLikeJpaRepository;

    @Override
    public boolean add(Long boardId, Long userId) {
        if (exists(boardId, userId)) return false;
        boardLikeJpaRepository.save(BoardLikeEntity.of(boardId, userId));
        return true;
    }

    @Override
    public boolean remove(Long boardId, Long userId) {
        List<BoardLikeEntity> likes = boardLikeJpaRepository.findAllByUserIdAndBoardIdIn(userId, List.of(boardId));
        if (likes.isEmpty()) return false;
        boardLikeJpaRepository.deleteAll(likes);
        return true;
    }

    @Override
    public boolean exists(Long boardId, Long userId) {
        return boardLikeJpaRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    @Override
    public int countByBoardId(Long boardId) {
        return boardLikeJpaRepository.countByBoardId(boardId);
    }

    @Override
    public Set<Long> findAllByUserIdAndBoardIds(List<Long> boardIds, Long userId) {
        return boardLikeJpaRepository.findAllByUserIdAndBoardIdIn(userId, boardIds)
                .stream()
                .map(entity -> entity.getBoard().getId())
                .collect(Collectors.toSet());
    }
}