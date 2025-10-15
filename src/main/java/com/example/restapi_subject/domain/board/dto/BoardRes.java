package com.example.restapi_subject.domain.board.dto;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.comment.dto.CommentRes;

import java.time.LocalDateTime;

public record BoardRes() {
    public record BoardDto(
            Long id,
            Long authorId,
            String title,
            String content,
            String image,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            int viewCount,
            int likeCount,
            int commentCount,
            boolean likedByMe
    ){
        public static BoardDto from(Board b, boolean likedByMe) {
            return new BoardDto(
                    b.getId(),
                    b.getAuthorId(),
                    b.getTitle(),
                    b.getContent(),
                    b.getImage(),
                    b.getCreatedDate(),
                    b.getUpdatedDate(),
                    b.getViewCount(),
                    b.getLikeCount(),
                    b.getCommentCount(),
                    likedByMe
            );
        }

    }
    public record CreateIdDto(Long boardId) {
        public static CreateIdDto of(Long id) {
            return new CreateIdDto(id);
        }
    }

    public record LikeDto(Long boardId, int likeCount, boolean likedByMe) {
        public static LikeDto of(Long boardId, int likeCount, boolean likedByMe) {
            return new LikeDto(boardId, likeCount, likedByMe);
        }
    }

    public static record DetailDto(
            BoardDto board,
            CommentRes.PageDto<CommentRes.CommentDto> comments
    ) {}
}
