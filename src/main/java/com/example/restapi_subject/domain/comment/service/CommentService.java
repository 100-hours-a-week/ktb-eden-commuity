package com.example.restapi_subject.domain.comment.service;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.board.service.BoardValidator;
import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.dto.CommentReq;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BoardValidator boardValidator;

    public CommentRes.PageDto<CommentRes.CommentDto> list(Long boardId, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findWithAuthor(boardId, pageable);
        List<CommentRes.CommentDto> items = comments.getContent().stream()
                .map(CommentRes.CommentDto::from)
                .toList();
        return new CommentRes.PageDto<>(items, page, size, comments.getTotalPages(), (int)comments.getTotalElements());
    }

    @Transactional
    public CommentRes.CreateIdDto create(Long boardId, Long authorId, CommentReq.CreateDto dto) {
        boardValidator.ensureBoardExists(boardId);
        Comment saved = commentRepository.save(Comment.create(boardId, authorId, dto.content()));
        boardRepository.updateCommentCount(boardId, +1);
        return CommentRes.CreateIdDto.of(saved.getId());
    }

    @Transactional
    public CommentRes.CommentDto update(Long boardId, Long commentId, Long requesterId, CommentReq.UpdateDto dto) {
        Comment c = getCommentOrThrow(commentId);
        checkEditableOrThrow(c, boardId, requesterId);

        c.changeContent(dto.content());
        c = commentRepository.save(c);
        return CommentRes.CommentDto.from(c);
    }

    @Transactional
    public void delete(Long boardId, Long commentId, Long requesterId) {
        Comment c = getCommentOrThrow(commentId);
        checkEditableOrThrow(c, boardId, requesterId);
        commentRepository.softDeleteById(commentId);
        boardRepository.updateCommentCount(boardId, -1);
    }

    /**
     * 내부 메서드
     */

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionType.COMMENT_NOT_FOUND));
    }

    private void checkEditableOrThrow(Comment c, Long boardId, Long requesterId) {
        if (!c.canEdit(requesterId)) throw new CustomException(ExceptionType.ACCESS_DENIED);
        if (!c.getBoardId().equals(boardId)) throw new CustomException(ExceptionType.ACCESS_DENIED);
    }
}
