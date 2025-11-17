package com.example.restapi_subject.domain.comment.service;

import com.example.restapi_subject.domain.board.service.BoardValidator;
import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentManagementFacade {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BoardValidator boardValidator;

    public CommentRes.PageDto<CommentRes.CommentDto> list(Long boardId, int page, int size) {
        boardValidator.ensureBoardExists(boardId);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        int totalElements = commentRepository.countByBoardId(boardId);
        int totalPages = (totalElements + size - 1) / size;

        List<Comment> comments = commentRepository.findByBoardIdPaged(boardId, page, size);

        Set<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());

        Map<Long, UserRes.SimpleProfileDto> profileMap = userService.getProfilesByIds(authorIds);

        List<CommentRes.CommentDto> items = comments.stream()
                .map(c -> {
                    UserRes.SimpleProfileDto profileDto = profileMap.get(c.getAuthorId());
                    String nick = (profileDto != null) ? profileDto.nickname() : "탈퇴한 사용자";
                    String img  = (profileDto != null) ? profileDto.profileImage() : "/images/default.png";
                    return CommentRes.CommentDto.from(c, nick, img);
                })
                .toList();
        return new CommentRes.PageDto<>(items, page, size, totalPages, totalElements);
    }

}
