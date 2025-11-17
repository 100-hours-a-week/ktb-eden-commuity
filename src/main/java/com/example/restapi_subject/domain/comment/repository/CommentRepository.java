package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    void delete(Comment comment);
    List<Comment> findActiveByUserId(Long userId);
    void softDeleteById(Long commentId);
    void softDeleteByUserId(Long userId);
    List<Comment> findByBoardIdPaged(Long boardId, int page, int size);
    int countByBoardId(Long boardId);
}