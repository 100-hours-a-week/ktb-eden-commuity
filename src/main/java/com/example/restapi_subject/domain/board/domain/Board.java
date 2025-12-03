package com.example.restapi_subject.domain.board.domain;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String image;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<Comment> comments = new ArrayList<>();;
    private boolean deleted;

    @Builder
    private Board(Long id, Long authorId, String title, String content, String image, Integer viewCount, Integer likeCount, Integer commentCount, LocalDateTime createdDate, LocalDateTime updatedDate, List<Comment> comments, boolean deleted) {
        super(createdDate, updatedDate);
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = (viewCount == null ? 0 : viewCount);
        this.likeCount = (likeCount == null ? 0 : likeCount);
        this.commentCount = (commentCount == null ? 0 : commentCount);
        this.comments = comments;
        this.deleted = deleted;
    }

    public static Board create(Long authorId, String title, String content, String image) {
        return Board.builder()
                .authorId(authorId)
                .title(title)
                .content(content)
                .image(image)
                .build();
    }

    //  인메모리 저장용 임시 메서드
    public Board withId(Long id){
        if(this.id != null) throw new CustomException(ExceptionType.BOARD_ALREADY_CREATED);
        Board copy = Board.builder()
                .authorId(this.authorId)
                .title(this.title)
                .content(this.content)
                .image(this.image)
                .viewCount(this.viewCount)
                .likeCount(this.likeCount)
                .commentCount(this.commentCount)
                .deleted(this.deleted)
                .build();
        copy.id = id;
        return copy;
    }

    public void softDelete() {
        this.deleted = true;
        touch();
    }

    public void increaseView() { this.viewCount++; }
    public void increaseLike() { this.likeCount++; }
    public void decreaseLike() {
        if (this.likeCount == 0) return; // 또는 예외
        this.likeCount--;
    }
    public void increaseComment() { this.commentCount++; }
    public void decreaseComment() {
        if (this.commentCount == 0) return; // 또는 예외
        this.commentCount--;
    }

    public void changeTitle(String title) {
        if (title == null || title.isBlank()) throw new CustomException(ExceptionType.TITLE_REQUIRED);
        this.title = title; touch();
    }
    public void changeContent(String content) {
        if (content == null || content.isBlank()) throw new CustomException(ExceptionType.CONTENT_REQUIRED);
        this.content = content; touch();
    }
    public void changeImage(String image) { this.image = image; touch(); }
}
