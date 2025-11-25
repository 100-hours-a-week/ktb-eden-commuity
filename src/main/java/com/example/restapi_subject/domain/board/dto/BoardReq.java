package com.example.restapi_subject.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardReq {
    @Schema(name = "BoardReqCreateDto")
    public static record CreateDto(
            @NotBlank(message = "title_required")
            @Size(max = 26, message = "title_max_26")
            String title,
            @NotBlank(message = "content_required")
            String content,
            String image
    ){
    }

    @Schema(name = "BoardReqUpdateDto")
    public static record UpdateDto(
            @Size(max = 26, message = "title_max_26")
            String title,
            String content,
            String image
    ) {}
}
