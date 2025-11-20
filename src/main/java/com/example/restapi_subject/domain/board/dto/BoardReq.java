package com.example.restapi_subject.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardReq(
) {
    public record CreateDto(
            @Schema(name = "BoardReqCreateDto")
            @NotBlank(message = "title_required")
            @Size(max = 26, message = "title_max_26")
            String title,
            @NotBlank(message = "content_required")
            String content,
            String image
    ){
    }

    public record UpdateDto(
            @Schema(name = "BoardReqUpdateDto")
            @Size(max = 26, message = "title_max_26")
            String content,
            String title,
            String image
    ) {}
}
