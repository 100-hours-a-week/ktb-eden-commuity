package com.example.restapi_subject.domain.boardlike.repository;

import com.example.restapi_subject.domain.boardlike.infra.BoardLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeJpaRepository extends JpaRepository<BoardLikeEntity, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    int countByBoardId(Long boardId);

    @Modifying
    @Query("DELETE FROM BoardLikeEntity bl WHERE bl.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    List<BoardLikeEntity> findAllByUserIdAndBoardIdIn(Long userId, List<Long> boardIds);

    @Query("SELECT b.board.id FROM BoardLikeEntity b WHERE b.user.id = :userId")
    List<Long> findBoardIdsByUserId(Long userId);
}
