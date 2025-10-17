package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCommentRepository {

    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public Comment save(Comment c) {
        if (c.getId() == null) c = c.withId(seq.incrementAndGet());
        store.put(c.getId(), c);
        return c;
    }

    public Optional<Comment> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public List<Comment> findByBoardIdPaged(Long boardId, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        return store.values().stream()
                .filter(c -> Objects.equals(c.getBoardId(), boardId))
                .sorted(Comparator.comparing(Comment::getId))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    public int countByBoardId(Long boardId) {
        return (int) store.values().stream()
                .filter(c -> Objects.equals(c.getBoardId(), boardId))
                .count();
    }

    public void delete(Long id) { store.remove(id); }
}
