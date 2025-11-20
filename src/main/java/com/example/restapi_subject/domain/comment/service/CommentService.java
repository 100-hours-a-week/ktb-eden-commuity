package com.example.restapi_subject.domain.comment.service;

import com.example.restapi_subject.domain.board.event.CommentEvent;
import com.example.restapi_subject.domain.board.service.BoardValidator;
import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.dto.CommentReq;
import com.example.restapi_subject.domain.comment.dto.CommentRes;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    // TODO : soft delete 고려

    private final CommentRepository commentRepository;
    private final BoardValidator boardValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentRes.CreateIdDto create(Long boardId, Long authorId, CommentReq.CreateDto dto) {
        boardValidator.ensureBoardExists(boardId);
        Comment saved = commentRepository.save(Comment.create(boardId, authorId, dto.content()));
        eventPublisher.publishEvent(new CommentEvent(boardId, CommentEvent.Type.CREATED));

        return CommentRes.CreateIdDto.of(saved.getId());
    }

    public CommentRes.PageDto<CommentRes.CommentDto> list(Long boardId, int page, int size) {
        boardValidator.ensureBoardExists(boardId);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        int totalElements = commentRepository.countByBoardId(boardId);
        int totalPages = (totalElements + size - 1) / size;

        List<CommentRes.CommentDto> items = commentRepository.findByBoardIdPaged(boardId, page, size)
                .stream()
                .map(CommentRes.CommentDto::from)
                .toList();
        return new CommentRes.PageDto<>(items, page, size, totalPages, totalElements);
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
        commentRepository.delete(c);
        eventPublisher.publishEvent(new CommentEvent(boardId, CommentEvent.Type.DELETED));
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
        if (!c.canEdit(requesterId)) throw new CustomException(ExceptionType.ACCESS_DENIED);
    }
}
