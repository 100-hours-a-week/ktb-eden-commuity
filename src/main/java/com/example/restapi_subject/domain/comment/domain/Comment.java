package com.example.restapi_subject.domain.comment.domain;

import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long authorId;
    private String content;
    private boolean deleted;

    @Transient
    private String authorNickname;
    private String authorProfileImage;

    @Builder
    private Comment(Long id, Long boardId, Long authorId, String content, LocalDateTime createdDate, LocalDateTime updateDate, boolean deleted) {
        super(createdDate, updateDate);
        this.id = id;
        this.boardId = boardId;
        this.authorId = authorId;
        this.content = content;
        this.deleted = deleted;
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
                .deleted(this.deleted)
                .build();
        c.id = id;
        return c;
    }

    public void softDelete() {
        this.deleted = true;
        touch();
    }


    public boolean canEdit(Long requesterId) { return this.authorId != null && this.authorId.equals(requesterId); }

    public void changeContent(String content) {
        if (content == null || content.isBlank()) throw new CustomException(ExceptionType.CONTENT_REQUIRED);
        this.content = content; touch();
    }

    public Comment withAuthorInfo(String nickname, String profileImage) {
        this.authorNickname = nickname;
        this.authorProfileImage = profileImage;
        return this;
    }
}
