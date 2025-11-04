package com.example.restapi_subject.domain.boardlike.infra;

import com.example.restapi_subject.domain.board.infra.BoardEntity;
import com.example.restapi_subject.domain.boardlike.domain.BoardLike;
import com.example.restapi_subject.domain.user.infra.UserEntity;
import com.example.restapi_subject.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "board_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"board_id", "user_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLikeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardlike_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    private BoardLikeEntity (Long id, BoardEntity board, UserEntity user) {
        super();
        this.id = id;
        this.board = board;
        this.user = user;
    }

    public static BoardLikeEntity of(Long boardId, Long userId) {
        BoardLikeEntity entity = new BoardLikeEntity();
        entity.board = BoardEntity.of(boardId);
        entity.user = UserEntity.of(userId);
        return entity;
    }

    public static BoardLikeEntity from(BoardLike like, BoardEntity board, UserEntity user) {
        return BoardLikeEntity.builder()
                .id(like.getId())
                .board(board)
                .user(user)
                .build();
    }

    public BoardLike toDomain() {
        return BoardLike.builder()
                .id(this.id)
                .boardId(this.board.getId())
                .userId(this.user.getId())
                .build();
    }
}