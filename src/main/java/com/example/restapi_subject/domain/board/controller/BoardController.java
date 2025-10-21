package com.example.restapi_subject.domain.board.controller;

import com.example.restapi_subject.domain.board.dto.BoardReq;
import com.example.restapi_subject.domain.board.dto.BoardRes;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.board.service.BoardService;
import com.example.restapi_subject.domain.comment.service.CommentService;
import com.example.restapi_subject.global.common.dto.PageCursor;
import com.example.restapi_subject.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "게시글 목록 조회(커서)", description = "커서(null이면 첫페이지) 기반으로 게시글(default=10개)을 조회합니다.(비회원도 가능)")
    public ApiResponse<PageCursor<BoardRes.BoardDto>> list(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResponse.ok("board_list_success", boardService.listByCursor(userId, cursorId, pageSize));
    }

    // TODO : 비회원 게시글 작성 고려
    @PostMapping
    @Operation(summary = "게시글 생성", description = "토큰(AT)검증후 게시글을 생성합니다.")
    public ApiResponse<BoardRes.CreateIdDto> create(@RequestAttribute("userId") Long userId,
                                                    @Valid @RequestBody BoardReq.CreateDto dto) {
        return ApiResponse.ok("board_created_success", boardService.create(userId, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "해당 게시글을 상세 조회합니다.(비회원도 가능)")
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
    @Operation(summary = "게시글 수정", description = "토큰(AT)검증후 게시글을 수정합니다.")
    public ApiResponse<BoardRes.BoardDto> update(@PathVariable("id") Long boardId,
                                                 @RequestAttribute("userId") Long userId,
                                                 @Valid @RequestBody BoardReq.UpdateDto dto) {
        return ApiResponse.ok("board_update_success", boardService.update(boardId, userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "토큰(AT)검증후 게시글을 삭제합니다.")
    public ApiResponse<Void> delete(@PathVariable("id") Long boardId,
                                    @RequestAttribute("userId") Long userId) {
        boardService.delete(boardId, userId);
        return ApiResponse.ok("board_deleted_success", null);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요", description = "토큰(AT)검증후 게시글 좋아요를 추가합니다.")
    public ApiResponse<BoardRes.LikeDto> like(@PathVariable("id") Long boardId,
                                               @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_liked_success", boardService.like(boardId, userId));
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "토큰(AT)검증후 게시글 좋아요를 취소합니다.")
    public ApiResponse<BoardRes.LikeDto> unlike(@PathVariable("id") Long boardId,
                                                 @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_unliked_success", boardService.unlike(boardId, userId));
    }
}
