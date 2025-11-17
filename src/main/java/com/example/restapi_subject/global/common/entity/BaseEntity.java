package com.example.restapi_subject.global.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    protected BaseEntity() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    protected BaseEntity(LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.createdDate = (createdDate != null ? createdDate : LocalDateTime.now());
        this.updatedDate = (updatedDate != null ? updatedDate : LocalDateTime.now());
    }

    protected void touch() {
        this.updatedDate = LocalDateTime.now();
    }

}
