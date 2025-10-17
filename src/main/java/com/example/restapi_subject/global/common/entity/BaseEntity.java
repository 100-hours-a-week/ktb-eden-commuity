package com.example.restapi_subject.global.common.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseEntity {

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    protected BaseEntity() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    protected void touch() {
        this.updatedDate = LocalDateTime.now();
    }

}
