package com.example.restapi_subject.domain.auth.infra;


import com.example.restapi_subject.domain.auth.domain.RefreshToken;
import com.example.restapi_subject.global.common.entity.JpaBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends JpaBaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 512)
    private String token;

    @Builder
    private RefreshTokenEntity (Long id, Long userId, String token) {
        super();
        this.id = id;
        this.userId = userId;
        this.token = token;
    }

    public static RefreshTokenEntity of(Long userId, String token) {
        return RefreshTokenEntity.builder()
                .userId(userId)
                .token(token)
                .build();
    }

    public static RefreshTokenEntity from(RefreshToken domain) {
        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .build();
    }

    public RefreshToken toDomain() {
        return RefreshToken.builder()
                .id(this.id)
                .userId(this.userId)
                .token(this.token)
                .build();
    }

    public void update(String newToken) {
        this.token = newToken;
        touch();
    }
}