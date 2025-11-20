package com.example.restapi_subject.domain.comment.infra;


import com.example.restapi_subject.domain.board.infra.BoardEntity;
import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.user.infra.UserEntity;
import com.example.restapi_subject.global.common.entity.JpaBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false)
    private String content;

    @Builder
    private CommentEntity(Long id, BoardEntity board, UserEntity author, String content) {
        super();
        this.id = id;
        this.board = board;
        this.author = author;
        this.content = content;
    }

    public static CommentEntity of(Long boardId, Long authorId, String content) {
        return CommentEntity.builder()
                .board(BoardEntity.of(boardId))
                .author(UserEntity.of(authorId))
                .content(content)
                .build();
    }

    public static CommentEntity from(Comment comment) {
        return CommentEntity.builder()
                .id(comment.getId())
                .board(BoardEntity.of(comment.getBoardId()))
                .author(UserEntity.of(comment.getAuthorId()))
                .content(comment.getContent())
                .build();
    }

    public Comment toDomain() {
        return Comment.builder()
                .id(this.id)
                .boardId(this.board.getId())
                .authorId(this.author.getId())
                .content(this.content)
                .build();
    }
}