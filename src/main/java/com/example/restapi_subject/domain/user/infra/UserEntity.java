package com.example.restapi_subject.domain.user.infra;

import com.example.restapi_subject.domain.board.infra.BoardEntity;
import com.example.restapi_subject.domain.boardlike.infra.BoardLikeEntity;
import com.example.restapi_subject.domain.comment.infra.CommentEntity;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImage;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLikeEntity> boardLikes = new ArrayList<>();


    @Builder
    private UserEntity (Long id, String email, String password, String nickname, String profileImage) {
        super();
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static UserEntity of(Long id) {
        UserEntity user = new UserEntity();
        user.id = id;
        return user;
    }

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .build();
    }
}