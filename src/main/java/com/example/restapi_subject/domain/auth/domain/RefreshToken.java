package com.example.restapi_subject.domain.auth.domain;


import com.example.restapi_subject.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    private Long id;
    private Long userId;
    private String token;

    @Builder
    private RefreshToken(Long id, Long userId, String token) {
        super();
        this.id = id;
        this.userId = userId;
        this.token = token;
    }

    public static RefreshToken create(Long userId, String token) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .build();
    }

    public void update(String newToken) {
        this.token = newToken;
        touch();
    }
}