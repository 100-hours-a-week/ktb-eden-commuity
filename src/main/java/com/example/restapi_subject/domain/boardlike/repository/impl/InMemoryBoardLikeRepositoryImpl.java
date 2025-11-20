package com.example.restapi_subject.domain.boardlike.repository.impl;

import com.example.restapi_subject.domain.boardlike.repository.BoardLikeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class InMemoryBoardLikeRepositoryImpl implements BoardLikeRepository {

    private final Map<Long, Set<Long>> likesByUser = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> likesByBoard = new ConcurrentHashMap<>();

    @Override
    public boolean add(Long boardId, Long userId) {
        likesByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(boardId);
        return likesByBoard.computeIfAbsent(boardId, k -> ConcurrentHashMap.newKeySet()).add(userId);

    }

    @Override
    public boolean remove(Long boardId, Long userId) {
        boolean removed = likesByBoard.getOrDefault(boardId, Set.of()).remove(userId);
        likesByUser.getOrDefault(userId, Set.of()).remove(boardId);

        return removed;
    }

    @Override
    public boolean exists(Long boardId, Long userId) {
        Set<Long> set = likesByBoard.get(boardId);
        return set != null && set.contains(userId);
    }

    @Override
    public int countByBoardId(Long boardId) {
        return Optional.ofNullable(likesByBoard.get(boardId))
                .map(Set::size)
                .orElse(0);
    }

    @Override
    public Set<Long> findAllByUserIdAndBoardIds(List<Long> boardIds,Long userId) {
        Set<Long> likedBoards = likesByUser.getOrDefault(userId, Set.of());
        return boardIds.stream()
                .filter(likedBoards::contains)
                .collect(Collectors.toSet());
    }
}