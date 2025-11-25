package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    void delete(Comment comment);
    List<Comment> findActiveByUserId(Long userId);
    void softDeleteById(Long commentId);
    void softDeleteByUserId(Long userId);
    Page<Comment> findByBoardId(Long boardId, Pageable pageable);
}