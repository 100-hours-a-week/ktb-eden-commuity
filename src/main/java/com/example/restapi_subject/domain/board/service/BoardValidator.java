package com.example.restapi_subject.domain.board.service;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardValidator {
    private final BoardRepository boardRepository;

    public void ensureBoardExists(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(ExceptionType.BOARD_NOT_FOUND);
        }
    }
}