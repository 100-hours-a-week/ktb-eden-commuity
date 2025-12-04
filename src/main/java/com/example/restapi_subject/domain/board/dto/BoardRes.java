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
                    b.getViewCount().get(),
                    b.getLikeCount().get(),
                    b.getCommentCount().get(),
                    likedByMe
            );
        }

    }
    public record CreateIdDto(Long boardId) {
        public static CreateIdDto of(Long id) {
            return new CreateIdDto(id);
        }
    }

    public record DetailDto(
            BoardDto board,
            CommentRes.PageDto<CommentRes.CommentDto> comments
    ) {
        public static DetailDto from(Board board, boolean liked, CommentRes.PageDto<CommentRes.CommentDto> comments) {
            return new DetailDto(BoardDto.from(board, liked), comments);
        }
    }
}
