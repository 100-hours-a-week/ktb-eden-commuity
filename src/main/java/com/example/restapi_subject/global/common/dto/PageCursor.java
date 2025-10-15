package com.example.restapi_subject.global.common.dto;

import java.util.List;

public record PageCursor<T> (
        List<T> content,
        boolean hasNext,
        Long nextCursor
) { }
