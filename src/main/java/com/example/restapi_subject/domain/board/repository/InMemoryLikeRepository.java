package com.example.restapi_subject.domain.board.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLikeRepository {

    private final Map<Long, Set<Long>> likes = new ConcurrentHashMap<>();

    public boolean add(Long boardId, Long userId) {
        return likes.computeIfAbsent(boardId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public boolean remove(Long boardId, Long userId) {
        Set<Long> set = likes.get(boardId);
        if (set == null) return false;
        boolean removed = set.remove(userId);
        if (set.isEmpty()) likes.remove(boardId);
        return removed;
    }

    public boolean exists(Long boardId, Long userId) {
        Set<Long> set = likes.get(boardId);
        return set != null && set.contains(userId);
    }
}