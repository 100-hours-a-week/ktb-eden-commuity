package com.example.restapi_subject.domain.boardlike.controller;

import com.example.restapi_subject.domain.boardlike.dto.BoardLikeResDto;
import com.example.restapi_subject.domain.boardlike.service.BoardLikeService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요", description = "토큰(AT)검증후 게시글 좋아요를 추가합니다.")
    public ApiResponse<BoardLikeResDto.LikeDto> like(@PathVariable("id") Long boardId,
                                                     @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_liked_success", boardLikeService.like(boardId, userId));
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "토큰(AT)검증후 게시글 좋아요를 취소합니다.")
    public ApiResponse<BoardLikeResDto.LikeDto> unlike(@PathVariable("id") Long boardId,
                                                 @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("board_unliked_success", boardLikeService.unlike(boardId, userId));
    }
}