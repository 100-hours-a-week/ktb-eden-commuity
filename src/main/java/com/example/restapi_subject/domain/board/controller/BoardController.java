package com.example.restapi_subject.domain.board.controller;

import com.example.restapi_subject.domain.board.dto.BoardReq;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.board.service.BoardService;
import com.example.restapi_subject.domain.comment.service.CommentService;
import com.example.restapi_subject.global.common.dto.PageCursor;
import com.example.restapi_subject.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping
    public ApiResponse<PageCursor<BoardRes.BoardDto>> list(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResponse.ok("board_list_success", boardService.listByCursor(userId, cursorId, pageSize));
    }

    @PostMapping
    public ApiResponse<BoardRes.CreateIdDto> create(@RequestAttribute("userId") Long userId,
                                                    @Valid @RequestBody BoardReq.CreateDto dto) {
        return ApiResponse.ok("board_created_success", boardService.create(userId, dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<BoardRes.DetailDto> get(
            @PathVariable("id") Long boardId,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardRes.BoardDto board = boardService.get(boardId, userId);
        CommentRes.PageDto<CommentRes.CommentDto> comments = commentService.list(boardId, page, size);
        return ApiResponse.ok("board_detail_success", new BoardRes.DetailDto(board, comments));
    }

    @PatchMapping("/{id}")
    public ApiResponse<BoardRes.BoardDto> update(@PathVariable("id") Long boardId,
                                                 @RequestAttribute("userId") Long userId,
                                                 @Valid @RequestBody BoardReq.UpdateDto dto) {
        return ApiResponse.ok("board_update_success", boardService.update(boardId, userId, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long boardId,
                                    @RequestAttribute("userId") Long userId) {
        boardService.delete(boardId, userId);
        return ApiResponse.ok("board_deleted_success", null);
    }

    @PostMapping("/{id}/like")
    public ApiResponse<BoardRes.LikeDto> like(@PathVariable("id") Long boardId,
                                               @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_liked_success", boardService.like(boardId, userId));
    }

    @DeleteMapping("/{id}/like")
    public ApiResponse<BoardRes.LikeDto> unlike(@PathVariable("id") Long boardId,
                                                 @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_unliked_success", boardService.unlike(boardId, userId));
    }
}
