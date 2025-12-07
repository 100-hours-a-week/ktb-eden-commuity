package com.example.restapi_subject.domain.comment.repository.impl;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import com.example.restapi_subject.domain.comment.repository.CommentJpaRepository;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaCommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            CommentEntity newEntity = CommentEntity.from(comment);
            return commentJpaRepository.save(newEntity).toDomain();
        }

        CommentEntity existing = commentJpaRepository.findById(comment.getId())
                .orElseThrow(() -> new CustomException(ExceptionType.COMMENT_NOT_FOUND));

        CommentEntity updated = CommentEntity.builder()
                .id(existing.getId())
                .board(existing.getBoard())
                .author(existing.getAuthor())
                .content(comment.getContent())
                .build();

        return commentJpaRepository.save(updated).toDomain();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id)
                .map(CommentEntity::toDomain);
    }

    @Override
    public void delete(Comment comment) {
        CommentEntity existing = commentJpaRepository.findById(comment.getId())
                .orElseThrow(() -> new CustomException(ExceptionType.COMMENT_NOT_FOUND));
        commentJpaRepository.delete(existing);
    }

    @Override
    public List<Comment> findActiveByUserId(Long userId) {
        return commentJpaRepository.findAllByAuthor_IdAndDeletedFalse(userId)
                .stream()
                .map(CommentEntity::toDomain)
                .toList();
    }

    @Override
    public void softDeleteById(Long commentId) {
        commentJpaRepository.softDeleteById(commentId);
    }

    @Override
    public void softDeleteByUserId(Long userId) {
        commentJpaRepository.softDeleteByUserId(userId);
    }

    @Override
    public Page<Comment> findByBoardId(Long boardId, Pageable pageable) {
        return commentJpaRepository.findByBoardId(boardId, pageable)
                .map(CommentEntity::toDomain);
    }

    @Override
    public Page<Comment> findWithAuthor(Long boardId, Pageable pageable) {
        return commentJpaRepository.findWithAuthor(boardId, pageable)
                .map(CommentEntity::toDomainWithAuthor);
    }
}
