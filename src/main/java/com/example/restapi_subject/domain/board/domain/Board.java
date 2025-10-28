package com.example.restapi_subject.domain.board.domain;

import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String image;

    // TODO : DB 도입 시 원자적 쿼리로 개선 고려
    private AtomicInteger viewCount;
    private AtomicInteger likeCount;
    private AtomicInteger commentCount;

    @Builder
    private Board(Long authorId, String title, String content, String image, AtomicInteger viewCount, AtomicInteger likeCount, AtomicInteger commentCount) {
        super();
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = (viewCount == null ? new AtomicInteger(0) : viewCount);
        this.likeCount = (likeCount == null ? new AtomicInteger(0) : likeCount);
        this.commentCount = (commentCount == null ? new AtomicInteger(0) : commentCount);
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
    public void increaseView() { this.viewCount.incrementAndGet(); }
    public void increaseLike() { this.likeCount.incrementAndGet(); }
    public void decreaseLike() {
        if (this.likeCount.get() == 0) return; // 또는 예외
        this.likeCount.decrementAndGet();
    }
    public void increaseComment() { this.commentCount.incrementAndGet(); }
    public void decreaseComment() {
        if (this.commentCount.get() == 0) return; // 또는 예외
        this.commentCount.decrementAndGet();
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
