package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByBoardId(Long boardId, Pageable pageable);
    int countByBoardId(Long boardId);
}
