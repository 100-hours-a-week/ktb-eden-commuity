package com.example.restapi_subject.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardReq(
) {
    public record CreateDto(
            @NotBlank(message = "title_required")
            @Size(max = 26, message = "title_max_26")
            String title,
            @NotBlank(message = "content_required")
            String content,
            String image
    ){
    }

    public record UpdateDto(
            @Size(max = 26, message = "title_max_26")
            String content,
            String title,
            String image
    ) {}
}
