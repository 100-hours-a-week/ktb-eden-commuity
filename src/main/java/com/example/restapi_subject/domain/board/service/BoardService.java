package com.example.restapi_subject.domain.board.service;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.dto.BoardReq;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    // TODO : 페이지네이션 유틸화 고려
    private final BoardRepository boardRepository;

    @Transactional
    public BoardRes.CreateIdDto create(Long authorId, BoardReq.CreateDto dto) {
        Board board = Board.create(authorId, dto.title(), dto.content(), dto.image());
        return BoardRes.CreateIdDto.of(boardRepository.save(board).getId());
    }

    @Transactional
    public Board getBoardAndIncreaseViewOrThrow(Long boardId) {
        return boardRepository.update(boardId, cur -> {
                    cur.increaseView();
                    return cur;
                })
                .orElseThrow(() -> new CustomException(ExceptionType.BOARD_NOT_FOUND));
    }

    public Board getBoardOrThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ExceptionType.BOARD_NOT_FOUND));
    }

    @Transactional
    public BoardRes.BoardDto update(Long boardId, Long requesterId, BoardReq.UpdateDto dto) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        b = updateBoard(dto, b);
        return BoardRes.BoardDto.from(b, false);
    }

    @Transactional
    public void delete(Long boardId, Long requesterId) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        boardRepository.softDeleteById(boardId);
    }

    /**
     * 내부 메서드
     */

    private static void checkAuthorOrThrow(Long requesterId, Board b) {
        if (!b.getAuthorId().equals(requesterId)) {
            throw new CustomException(ExceptionType.ACCESS_DENIED);
        }
    }

    private Board updateBoard(BoardReq.UpdateDto dto, Board b) {
        if (dto.title() != null) {
            if (dto.title().isBlank()) throw new CustomException(ExceptionType.TITLE_REQUIRED);
            b.changeTitle(dto.title());
        }
        if (dto.content() != null) {
            if (dto.content().isBlank()) throw new CustomException(ExceptionType.CONTENT_REQUIRED);
            b.changeContent(dto.content());
        }
        if (dto.image() != null) {
            b.changeImage(dto.image());
        }
        b = boardRepository.save(b);
        return b;
    }
}
