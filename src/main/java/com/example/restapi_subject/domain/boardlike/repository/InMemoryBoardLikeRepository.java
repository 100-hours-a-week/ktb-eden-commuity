package com.example.restapi_subject.domain.boardlike.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryBoardLikeRepository implements BoardLikeRepository {

    private final Map<Long, Set<Long>> likedsByUser = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> likedsByBoard = new ConcurrentHashMap<>();

    @Override
    public boolean add(Long boardId, Long userId) {
        likedsByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(boardId);
        return likedsByBoard.computeIfAbsent(boardId, k -> ConcurrentHashMap.newKeySet()).add(userId);

    }

    @Override
    public boolean remove(Long boardId, Long userId) {
        boolean removed = likedsByBoard.getOrDefault(boardId, Set.of()).remove(userId);
        likedsByUser.getOrDefault(userId, Set.of()).remove(boardId);

        return removed;
    }

    @Override
    public boolean exists(Long boardId, Long userId) {
        Set<Long> set = likedsByBoard.get(boardId);
        return set != null && set.contains(userId);
    }

    @Override
    public int countByBoardId(Long boardId) {
        return Optional.ofNullable(likedsByBoard.get(boardId))
                .map(Set::size)
                .orElse(0);
    }

    @Override
    public Set<Long> findAllByUserIdAndBoardIds(List<Long> boardIds,Long userId) {
        Set<Long> likedBoards = likedsByUser.getOrDefault(userId, Set.of());
        return boardIds.stream()
                .filter(likedBoards::contains)
                .collect(Collectors.toSet());
    }
}