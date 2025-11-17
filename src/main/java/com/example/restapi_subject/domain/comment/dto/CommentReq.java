package com.example.restapi_subject.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public class CommentReq {
    @Schema(name = "CommentReqCreateDto")
    public static record CreateDto(
            @Schema(description = "댓글 내용", example = "댓글 달고 갑니다~")
            @NotBlank(message = "content_required")
            String content
    ){}

    @Schema(name = "CommentReqUpdateDto")
    public static record UpdateDto(
            @Schema(description = "댓글 수정 내용", example = "수정된 댓글")
            @NotBlank(message = "content_required")
            String content
    ){}
}
