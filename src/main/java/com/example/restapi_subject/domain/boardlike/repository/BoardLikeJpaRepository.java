package com.example.restapi_subject.domain.boardlike.repository;

import com.example.restapi_subject.domain.boardlike.infra.BoardLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeJpaRepository extends JpaRepository<BoardLikeEntity, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    int countByBoardId(Long boardId);
    List<BoardLikeEntity> findAllByUserIdAndBoardIdIn(Long userId, List<Long> boardIds);
}
