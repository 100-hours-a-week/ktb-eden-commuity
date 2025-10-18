package com.example.restapi_subject.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;


public record CommentReq() {
    public record CreateDto(
            @NotBlank(message = "content_required")
            String content
    ){}

    public record UpdateDto(
            @NotBlank(message = "content_required")
            String content
    ){}
}
