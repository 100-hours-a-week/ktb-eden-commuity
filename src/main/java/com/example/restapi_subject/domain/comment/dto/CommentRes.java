package com.example.restapi_subject.domain.comment.dto;

import com.example.restapi_subject.domain.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentRes() {

    public static record CommentDto(
            Long id,
            Long boardId,
            Long authorId,
            String authorNickname,
            String authorProfileImage,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate
    ) {
        public static CommentDto from(Comment c, String authorNickname, String authorProfileImage) {
            return new CommentDto(
                    c.getId(),
                    c.getBoardId(),
                    c.getAuthorId(),
                    authorNickname,
                    authorProfileImage,
                    c.isDeleted() ? "삭제된 댓글 입니다." : c.getContent(),
                    c.getCreatedDate(),
                    c.getUpdatedDate()
            );
        }
        public static CommentDto from(Comment c) {
            return new CommentDto(
                    c.getId(),
                    c.getBoardId(),
                    c.getAuthorId(),
                    c.getAuthorNickname(),
                    c.getAuthorProfileImage(),
                    c.isDeleted() ? "삭제된 댓글 입니다." : c.getContent(),
                    c.getCreatedDate(),
                    c.getUpdatedDate()
            );
        }
    }
    public record CreateIdDto(Long commentId) {
        public static CreateIdDto of(Long id) {
            return new CreateIdDto(id);
        }
    }

    public static record PageDto<T>(
            List<T> content,
            int page,
            int size,
            int totalPages,
            int totalElements
    ) {
        public static <T> PageDto<T> of(List<T> content) {
            int size = content.size();
            return new PageDto<>(content, 0, size, 1, size);
        }

        public static <T> PageDto<T> of(List<T> content, int page, int size, int totalPages, int totalElements) {
            return new PageDto<>(content, page, size, totalPages, totalElements);
        }
    }
}
