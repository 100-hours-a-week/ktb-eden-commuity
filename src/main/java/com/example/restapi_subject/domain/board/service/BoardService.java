package com.example.restapi_subject.domain.board.service;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.dto.BoardReq;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.board.repository.InMemoryBoardRepository;
import com.example.restapi_subject.domain.board.repository.InMemoryLikeRepository;
import com.example.restapi_subject.global.common.dto.PageCursor;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    // TODO : 페이지네이션 유틸화 고려

    private final InMemoryBoardRepository boardRepository;
    private final InMemoryLikeRepository likeRepository;

    public BoardRes.CreateIdDto create(Long authorId, BoardReq.CreateDto dto) {
        Board board = Board.create(authorId, dto.title(), dto.content(), dto.image());
        return BoardRes.CreateIdDto.of(boardRepository.save(board).getId());
    }

    public BoardRes.BoardDto get(Long boardId, Long userIdOrNull) {
        Board b = getBoardAndIncreaseViewOrThrow(boardId);
        return toDto(b, userIdOrNull);
    }

    public List<BoardRes.BoardDto> list(Long userIdOrNull) {
        return boardRepository.findAll().stream()
                .map(b -> toDto(b, userIdOrNull))
                .toList();
    }

    public PageCursor<BoardRes.BoardDto> listByCursor(Long userIdOrNull, Long cursorId, int pageSize) {
        List<Board> rows = boardRepository.findAllByCursor(cursorId, pageSize);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) rows = rows.subList(0, pageSize);

        Long nextCursorId = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();

        List<BoardRes.BoardDto> content = rows.stream()
                .map(b -> toDto(b, userIdOrNull))
                .toList();

        return new PageCursor<>(content, hasNext, nextCursorId);
    }

    public BoardRes.BoardDto update(Long boardId, Long requesterId, BoardReq.UpdateDto dto) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        b = updateBoard(dto, b);
        return toDto(b, requesterId);
    }

    public void delete(Long boardId, Long requesterId) {
        Board b = getBoardOrThrow(boardId);
        checkAuthorOrThrow(requesterId, b);
        boardRepository.delete(boardId);
    }

    public BoardRes.LikeDto like(Long boardId, Long userId) {
        Board b = getBoardOrThrow(boardId);

        if (likeRepository.add(boardId, userId)) {
            b.increaseLike();
            boardRepository.save(b);
        }
        boolean likedNow = likeRepository.exists(boardId, userId);
        return BoardRes.LikeDto.of(boardId, b.getLikeCount(), likedNow);
    }

    public BoardRes.LikeDto unlike(Long boardId, Long userId) {
        Board b = getBoardOrThrow(boardId);

        if (likeRepository.remove(boardId, userId)) {
            b.decreaseLike();
            boardRepository.save(b);
        }
        boolean likedNow = likeRepository.exists(boardId, userId);
        return BoardRes.LikeDto.of(boardId, b.getLikeCount(), likedNow);
    }

    /**
     * 내부 메서드
     */

    private BoardRes.BoardDto toDto(Board b, Long userIdOrNull) {
        return BoardRes.BoardDto.from(b, isLikedByMe(b, userIdOrNull));
    }

    private Board getBoardOrThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ExceptionType.BOARD_NOT_FOUND));
    }

    private Board getBoardAndIncreaseViewOrThrow(Long boardId) {
            return boardRepository.update(boardId, cur -> {
                cur.increaseView();
                return cur;
            })
            .orElseThrow(() -> new CustomException(ExceptionType.BOARD_NOT_FOUND));
    }

    private boolean isLikedByMe(Board b, Long userIdOrNull) {
        return (userIdOrNull != null) && likeRepository.exists(b.getId(), userIdOrNull);
    }

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
