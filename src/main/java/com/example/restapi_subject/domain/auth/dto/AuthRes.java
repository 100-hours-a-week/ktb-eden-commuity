package com.example.restapi_subject.domain.auth.dto;

public record AuthRes() {
    public record SignUpDto(
            Long userId
    ){}

    public record LoginDto(
            Long userId,
            TokenDto tokenDto
    ){}

    public record TokenDto(
            String accessToken,
            String refreshToken
    ){}
}
