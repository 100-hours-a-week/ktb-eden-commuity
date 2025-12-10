package com.example.restapi_subject.domain.boardlike.domain;

import com.example.restapi_subject.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long userId;

    @Builder
    public BoardLike(Long id, Long boardId, Long userId) {
        super();
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
    }

    public static BoardLike create(Long boardId, Long userId) {
        return BoardLike.builder()
                .boardId(boardId)
                .userId(userId)
                .build();
    }
}