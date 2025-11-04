package com.example.restapi_subject.domain.comment.repository.impl;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import com.example.restapi_subject.domain.comment.repository.CommentJpaRepository;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class JpaCommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = CommentEntity.of(
                comment.getBoardId(),
                comment.getAuthorId(),
                comment.getContent()
        );
        return commentJpaRepository.save(entity).toDomain();

    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id)
                .map(CommentEntity::toDomain);
    }

    @Override
    public void delete(Comment comment) {
        CommentEntity entity = CommentEntity.of(
                comment.getBoardId(),
                comment.getAuthorId(),
                comment.getContent()
        );
        commentJpaRepository.delete(entity);
    }

    @Override
    public List<Comment> findByBoardIdPaged(Long boardId, int page, int size) {
        return commentJpaRepository.findByBoardId(boardId, PageRequest.of(page, size))
                .stream()
                .map(CommentEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countByBoardId(Long boardId) {
        return commentJpaRepository.countByBoardId(boardId);
    }
}
