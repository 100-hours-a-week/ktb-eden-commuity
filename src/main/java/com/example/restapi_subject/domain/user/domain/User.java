package com.example.restapi_subject.domain.user.domain;


import com.example.restapi_subject.global.common.entity.BaseEntity;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private boolean deleted;

    @Builder
    private User (Long id, String email, String password, String nickname, String profileImage, boolean deleted) {
        super();
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.deleted = deleted;
    }

    public static User create(String email, String hashedPw, String nickname, String profileImage) {
        return User.builder()
                .email(email)
                .password(hashedPw)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

    //  인메모리 저장용 임시 메서드
    public User withId(Long id){
        if(this.id != null) throw new CustomException(ExceptionType.USER_ALREADY_CREATED);
        User copy = User.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .build();
        copy.id = id;
        return copy;
    }

    public void softDelete() {
        long timestamp = System.currentTimeMillis();
        this.deleted = true;
        this.email = this.email + ".deleted." + timestamp;
        this.nickname = this.nickname + ".deleted." + timestamp;
        touch();
    }

    public void restore() {
        if (!deleted) return;
        this.deleted = false;
        this.email = this.email.replaceFirst("\\.deleted\\.\\d+$", "");
        this.nickname = this.nickname.replaceFirst("\\.deleted\\.\\d+$", "");
        touch();
    }


    public void changeProfile(String nickname, String profileImage) {
        if (nickname != null) this.nickname = nickname;
        if (profileImage != null) this.profileImage = profileImage;
        touch();
    }

    public void changePasswordHashed(String hashedPw) {
        this.password = hashedPw;
        touch();
    }
}