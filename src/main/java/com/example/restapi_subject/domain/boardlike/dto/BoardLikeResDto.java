package com.example.restapi_subject.domain.boardlike.dto;

public record BoardLikeResDto() {

    public record LikeDto(Long boardId, int likeCount, boolean likedByMe) {
        public static LikeDto of(Long boardId, int likeCount, boolean likedByMe) {
            return new LikeDto(boardId, likeCount, likedByMe);
        }
    }
}
