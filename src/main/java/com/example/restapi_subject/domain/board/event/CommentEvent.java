package com.example.restapi_subject.domain.board.event;

public record CommentEvent(Long boardId, Type type) {
    public enum Type { CREATED, DELETED }
}
