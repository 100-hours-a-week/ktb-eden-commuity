package com.example.restapi_subject.domain.boardlike.event;

public record BoardLikeEvent(Long boardId, Long userId, Type type) {

    public enum Type { CREATED, DELETED }

    public static BoardLikeEvent created(Long boardId, Long userId) {
        return new BoardLikeEvent(boardId, userId, Type.CREATED);
    }

    public static BoardLikeEvent deleted(Long boardId, Long userId) {
        return new BoardLikeEvent(boardId, userId, Type.DELETED);
    }
}