package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.infra.BoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b from BoardEntity b where (:cursorId IS null OR b.id < :cursorId) order by b.id DESC ")
    List<BoardEntity> findAllByCursor(@Param("cursorId") Long cursorId, Pageable pageable);
}
