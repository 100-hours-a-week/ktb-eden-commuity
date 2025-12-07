package com.example.restapi_subject.domain.comment.event;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.domain.comment.repository.CommentRepository;
import com.example.restapi_subject.domain.user.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentEventListener {

    // TODO : 복구 로직

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    @Transactional
    public void onUserDeleted(UserEvent event) {
        if (event.type() != UserEvent.Type.DELETED) return;

        Long userId = event.userId();
        List<Comment> targets = commentRepository.findActiveByUserId(userId);

        if (targets.isEmpty()) return;
        commentRepository.softDeleteByUserId(userId);

        targets.stream()
                .collect(Collectors.groupingBy(Comment::getBoardId))
                .forEach((boardId, list) -> {
                    boardRepository.updateCommentCount(boardId, -list.size());
                });
    }
}
