package com.example.restapi_subject.domain.board.service;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.dto.BoardReq;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.boardlike.service.BoardLikeService;
import com.example.restapi_subject.global.common.dto.PageCursor;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardService {

    // TODO : 페이지네이션 유틸화 고려

    private final BoardRepository boardRepository;
    private final BoardLikeService boardLikeService;

    public BoardRes.CreateIdDto create(Long authorId, BoardReq.CreateDto dto) {
        Board board = Board.create(authorId, dto.title(), dto.content(), dto.image());
        return BoardRes.CreateIdDto.of(boardRepository.save(board).getId());
    }

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


    public PageCursor<BoardRes.BoardDto> listByCursorWithLikes(Long userId, Long cursorId, int pageSize) {
        PageCursor<Board> boardsCursor = listByCursor(cursorId, pageSize);
        List<Board> boards = boardsCursor.content();
        Set<Long> likedIds = (userId == null)
                ? Set.of() : boardLikeService.getLikedBoardIds(boards, userId);

        List<BoardRes.BoardDto> dtoList = boards.stream()
                .map(b -> BoardRes.BoardDto.from(b, likedIds.contains(b.getId())))
                .toList();

        return new PageCursor<>(dtoList, boardsCursor.hasNext(), boardsCursor.nextCursorId());
    }

    public BoardRes.BoardDto update(Long boardId, Long requesterId, BoardReq.UpdateDto dto) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        b = updateBoard(dto, b);
        return BoardRes.BoardDto.from(b, false);
    }

    public void delete(Long boardId, Long requesterId) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        boardRepository.delete(boardId);
    }

    public void ensureBoardExists(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(ExceptionType.BOARD_NOT_FOUND);
        }
    }

    /**
     * 내부 메서드
     */

    private static void checkAuthorOrThrow(Long requesterId, Board b) {
        if (!b.getAuthorId().equals(requesterId)) {
            throw new CustomException(ExceptionType.ACCESS_DENIED);
        }
    }

    private PageCursor<Board> listByCursor(Long cursorId, int pageSize) {
        List<Board> rows = boardRepository.findAllByCursor(cursorId, pageSize);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) rows = rows.subList(0, pageSize);
        Long nextCursorId = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();

        return new PageCursor<>(rows, hasNext, nextCursorId);
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
