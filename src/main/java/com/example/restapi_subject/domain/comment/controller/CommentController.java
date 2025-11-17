package com.example.restapi_subject.domain.comment.controller;

import com.example.restapi_subject.domain.comment.dto.CommentReq;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.service.CommentManagementFacade;
import com.example.restapi_subject.domain.comment.service.CommentService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}")
public class CommentController {

    private final CommentService commentService;
    private final CommentManagementFacade commentManagementFacade;

    @PostMapping("/comments")
    @Operation(summary = "댓글 생성", description = "토큰(AT)검증후 댓글을 생성합니다.")
    public ApiResponse<CommentRes.CreateIdDto> create(@PathVariable Long boardId,
                                    @RequestAttribute("userId") Long userId,
                                    @Valid @RequestBody CommentReq.CreateDto dto) {
        return ApiResponse.ok("comment_created_success", commentService.create(boardId, userId, dto));
    }

    @GetMapping("/comments")
    @Operation(summary = "댓글 조회(오프셋)", description = "댓글을 조회합니다.(비회원도 가능)")
    public ApiResponse<CommentRes.PageDto<CommentRes.CommentDto>> list(@PathVariable Long boardId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("comment_list_success", commentManagementFacade.list(boardId, page, size));
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "토큰(AT)검증후 댓글을 수정합니다.")
    public ApiResponse<CommentRes.CommentDto> update(@PathVariable Long boardId,
                                                     @PathVariable Long commentId,
                                                     @RequestAttribute("userId") Long userId,
                                                     @Valid @RequestBody CommentReq.UpdateDto dto) {
        return ApiResponse.ok("comment_updated_success", commentService.update(boardId, commentId, userId, dto));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "토큰(AT)검증후 댓글을 삭제합니다.")
    public ApiResponse<Void> delete(@PathVariable Long boardId,
                                    @PathVariable Long commentId,
                                    @RequestAttribute("userId") Long userId) {
        commentService.delete(boardId, commentId, userId);
        return ApiResponse.ok("comment_deleted_success", null);
    }

}
