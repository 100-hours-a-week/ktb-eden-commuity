package com.example.restapi_subject.domain.comment.repository.impl;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.global.common.repository.BaseInMemoryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("inmemory")
public class InMemoryCommentRepositoryImpl extends BaseInMemoryRepository<Comment> implements CommentRepository {

    @Override
    public List<Comment> findActiveByUserId(Long userId) {
        return store.values().stream()
                .filter(c -> Objects.equals(c.getAuthorId(), userId))
                .filter(c -> !c.isDeleted())
                .sorted(Comparator.comparing(Comment::getId))
                .toList();
    }

    @Override
    public void softDeleteById(Long commentId) {
        Comment comment = store.get(commentId);
        if (comment != null && !comment.isDeleted()) {
            comment.softDelete();
            store.put(commentId, comment);
        }
    }

    @Override
    public void softDeleteByUserId(Long userId) {
        store.values().forEach(comment -> {
            if (Objects.equals(comment.getAuthorId(), userId)) {
                comment.softDelete();
            }
        });
    }

    @Override
    public Page<Comment> findByBoardId(Long boardId, Pageable pageable) {
        List<Comment> filtered = store.values().stream()
                .filter(c -> Objects.equals(c.getBoardId(), boardId))
                .sorted(Comparator.comparing(Comment::getId))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<Comment> content = (start >= filtered.size())
                ? List.of()
                : filtered.subList(start, end);

        return new PageImpl<>(content, pageable, filtered.size());
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
