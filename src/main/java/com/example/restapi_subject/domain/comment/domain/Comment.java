package com.example.restapi_subject.domain.comment.domain;

import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long authorId;
    private String content;

    @Builder
    private Comment(Long boardId, Long authorId, String content) {
        super();
        this.boardId = boardId;
        this.authorId = authorId;
        this.content = content;
    }

    public static Comment create(Long boardId, Long authorId, String content) {
        if (content == null || content.isBlank()) throw new CustomException(ExceptionType.CONTENT_REQUIRED);
        return Comment.builder()
                .boardId(boardId)
                .authorId(authorId)
                .content(content)
                .build();
    }

    public Comment withId(Long id) {
        if (this.id != null) throw new CustomException(ExceptionType.COMMENT_ALREADY_CREATED);
        Comment c = Comment.builder()
                .boardId(this.boardId)
                .authorId(this.authorId)
                .content(this.content)
                .build();
        c.id = id;
        return c;
    }

    public boolean canEdit(Long requesterId) { return this.authorId != null && this.authorId.equals(requesterId); }

    public void changeContent(String content) {
        if (content == null || content.isBlank()) throw new CustomException(ExceptionType.CONTENT_REQUIRED);
        this.content = content; touch();
    }
}
