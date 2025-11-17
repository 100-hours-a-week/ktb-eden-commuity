package com.example.restapi_subject.domain.user.event;

public record UserEvent(Long userId, Type type) {
    public enum Type { DELETED }
}
