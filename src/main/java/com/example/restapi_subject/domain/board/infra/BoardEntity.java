package com.example.restapi_subject.domain.board.infra;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import com.example.restapi_subject.domain.user.infra.UserEntity;
import com.example.restapi_subject.global.common.entity.JpaBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardEntity extends JpaBaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    private String image;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    private boolean deleted = false;

    @Builder
    private BoardEntity(Long id, UserEntity author, String title, String content,
                        String image, int viewCount, int likeCount, int commentCount, boolean deleted) {
        super();
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.deleted = deleted;
    }

    public static BoardEntity of(Long id) {
        BoardEntity board = new BoardEntity();
        board.id = id;
        return board;
    }

    public static BoardEntity from(Board board, UserEntity authorEntity) {
        return BoardEntity.builder()
                .id(board.getId())
                .author(authorEntity)
                .title(board.getTitle())
                .content(board.getContent())
                .image(board.getImage())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .deleted(board.isDeleted())
                .build();
    }

    public Board toDomain() {
        return Board.builder()
                .id(this.id)
                .authorId(this.author.getId())
                .title(this.title)
                .content(this.content)
                .image(this.image)
                .viewCount(this.viewCount)
                .likeCount(this.likeCount)
                .commentCount(this.commentCount)
                .createdDate(this.getCreatedDate())
                .updatedDate(this.getUpdatedDate())
                .deleted(this.deleted)
                .build();
    }
}
