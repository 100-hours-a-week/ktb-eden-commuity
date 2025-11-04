package com.example.restapi_subject.domain.board.service;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.boardlike.service.BoardLikeService;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.service.CommentService;
import com.example.restapi_subject.global.common.dto.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardManagementFacade {

    private final BoardService boardService;
    private final CommentService commentService;
    private final BoardRepository boardRepository;
    private final BoardLikeService boardLikeService;

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

    @Transactional
    public BoardRes.DetailDto getDetailDto(Long boardId, Long userId, int page, int size) {
        Board board = boardService.getBoardAndIncreaseViewOrThrow(boardId);
        CommentRes.PageDto<CommentRes.CommentDto> comments = commentService.list(boardId, page, size);
        boolean liked = (userId != null) && boardLikeService.isLiked(boardId, userId);
        return BoardRes.DetailDto.from(board, liked, comments);
    }

    private PageCursor<Board> listByCursor(Long cursorId, int pageSize) {
        List<Board> rows = boardRepository.findAllByCursor(cursorId, pageSize);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) rows = rows.subList(0, pageSize);
        Long nextCursorId = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();

        return new PageCursor<>(rows, hasNext, nextCursorId);
    }
}