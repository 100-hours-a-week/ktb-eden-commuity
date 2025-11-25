package com.example.restapi_subject.domain.comment.event;

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

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentEventListener {

    // TODO : soft Delete + 익명화 -> DTO 에서 매핑?

    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    @Transactional
    public void onUserDeleted(UserEvent event) {
        if (event.type() != UserEvent.Type.DELETED) return;

        Long userId = event.userId();
        List<Comment> targets = commentRepository.findActiveByUserId(userId);

        if (targets.isEmpty()) {
            return;
        }
        commentRepository.softDeleteByUserId(userId);

        targets.forEach(comment -> {
            eventPublisher.publishEvent(new CommentEvent(comment.getBoardId(), CommentEvent.Type.DELETED));
        });
    }
}
