package com.example.restapi_subject.domain.comment.controller;

import com.example.restapi_subject.domain.comment.dto.CommentReq;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.service.CommentService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ApiResponse<CommentRes.CreateIdDto> create(@PathVariable Long boardId,
                                    @RequestAttribute("userId") Long userId,
                                    @Valid @RequestBody CommentReq.CreateDto dto) {
        return ApiResponse.ok("comment_created_success", commentService.create(boardId, userId, dto));
    }

    @GetMapping("/comments")
    public ApiResponse<CommentRes.PageDto<CommentRes.CommentDto>> list(@PathVariable Long boardId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("comment_list_success", commentService.list(boardId, page, size));
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentRes.CommentDto> update(@PathVariable Long boardId,
                                                     @PathVariable Long commentId,
                                                     @RequestAttribute("userId") Long userId,
                                                     @Valid @RequestBody CommentReq.UpdateDto dto) {
        return ApiResponse.ok("comment_updated_success", commentService.update(boardId, commentId, userId, dto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long boardId,
                                    @PathVariable Long commentId,
                                    @RequestAttribute("userId") Long userId) {
        commentService.delete(boardId, commentId, userId);
        return ApiResponse.ok("comment_deleted_success", null);
    }

}
