package com.example.restapi_subject.domain.user.dto;

import com.example.restapi_subject.domain.user.domain.User;

public record UserRes() {
    public record UserDto(
            Long id,
            String email,
            String nickname,
            String profileImage
    ) {
        public static UserDto from(User u) {
            return new UserDto(u.getId(), u.getEmail(), u.getNickname(), u.getProfileImage());
        }
    }

    public static record UpdateProfileDto(
            Long id,
            String nickname,
            String profileImage
    ) {
        public static UpdateProfileDto from(User u) {
            return new UpdateProfileDto(u.getId(), u.getNickname(), u.getProfileImage());
        }
    }
}
