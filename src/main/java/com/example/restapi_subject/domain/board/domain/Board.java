package com.example.restapi_subject.domain.board.domain;

import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    private Board(Long id, Long authorId, String title, String content, String image, Integer viewCount, Integer likeCount, Integer commentCount) {
        super();
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = (viewCount == null ? 0 : viewCount);
        this.likeCount = (likeCount == null ? 0 : likeCount);
        this.commentCount = (commentCount == null ? 0 : commentCount);
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
                .build();
        copy.id = id;
        return copy;
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
