package com.example.restapi_subject.domain.comment.repository.impl;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.global.common.repository.BaseInMemoryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("inmemory")
public class InMemoryCommentRepositoryImpl extends BaseInMemoryRepository<Comment> implements CommentRepository {

    @Override
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

    @Override
    public int countByBoardId(Long boardId) {
        return (int) store.values().stream()
                .filter(c -> Objects.equals(c.getBoardId(), boardId))
                .count();
    }

    @Override
    protected Long getId(Comment comment) {
        return comment.getId();
    }

    @Override
    protected Comment assignId(Comment comment, Long id) {
        return comment.withId(id);
    }
}
