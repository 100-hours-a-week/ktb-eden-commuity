package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.infra.BoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b from BoardEntity b where b.deleted = false AND (:cursorId IS null OR b.id < :cursorId) order by b.id DESC ")
    List<BoardEntity> findAllByCursor(@Param("cursorId") Long cursorId, Pageable pageable);

    @Query("""
        SELECT b FROM BoardEntity b
        JOIN FETCH b.author
        WHERE b.deleted = false
          AND (:cursorId IS NULL OR b.id < :cursorId)
        ORDER BY b.id DESC
    """)
    List<BoardEntity> findBoardsWithAuthor(Long cursorId, int limit);

    @Query("""
        SELECT b FROM BoardEntity b
        JOIN FETCH b.author
        WHERE b.id = :boardId
    """)
    Optional<BoardEntity> findBoardWithAuthor(Long boardId);

    @Query("""
        SELECT DISTINCT b
        FROM BoardEntity b
        JOIN FETCH b.author a
        WHERE b.id = :boardId
          AND b.deleted = false
    """)
    Optional<BoardEntity> findBoardWithDetail(@Param("boardId") Long boardId);

    /**
     * 게시글 단일 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE BoardEntity b
        SET b.deleted = true
        WHERE b.id = :boardId
        """)
    void softDeleteById(@Param("boardId") Long boardId);


    /**
     * 유저 모든 게시글 삭제(회원 탈퇴시)
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE BoardEntity b
        SET b.deleted = true
        WHERE b.author.id = :userId
        """)
    void softDeleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE BoardEntity b SET b.commentCount = b.commentCount + :delta WHERE b.id = :boardId")
    void updateCommentCount(@Param("boardId") Long boardId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE BoardEntity b SET b.viewCount = b.viewCount + :delta WHERE b.id = :boardId")
    void updateViewCount(@Param("boardId") Long boardId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE BoardEntity b SET b.likeCount = b.likeCount + :delta WHERE b.id = :boardId")
    void updateLikeCount(@Param("boardId") Long boardId, @Param("delta") int delta);


}
