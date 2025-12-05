package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    /**
     * 댓글 조회
     */
    @Query("""
        SELECT c
        FROM CommentEntity c
        WHERE c.board.id = :boardId
            AND c.deleted = false
            order by c.id ASC

    """)
    Page<CommentEntity> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("""
        SELECT c
        FROM CommentEntity c
        JOIN FETCH c.author
        WHERE c.board.id = :boardId
          AND c.deleted = false
        ORDER BY c.id ASC
    """)
    Page<CommentEntity> findWithAuthor(Long boardId, Pageable pageable);


    @Query("""
        SELECT COUNT(c)
        FROM CommentEntity c
        WHERE c.board.id = :boardId
            AND c.deleted = false
    """)
    int countByBoardId(@Param("boardId") Long boardId);

    /**
     * 댓글 단일 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE CommentEntity c
        SET c.deleted = true
        WHERE c.id = :commentId
        """)
    void softDeleteById(@Param("commentId") Long commentId);

    /**
     * 유저 모든 댓글 삭제(회원 탈퇴시)
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE CommentEntity c
        SET c.deleted = true
        WHERE c.author.id = :userId
        """)
    void softDeleteByUserId(@Param("userId") Long userId);
    List<CommentEntity> findAllByAuthor_IdAndDeletedFalse(Long userId);
}
