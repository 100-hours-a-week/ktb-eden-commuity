package com.example.restapi_subject.domain.boardlike.event;


import com.example.restapi_subject.domain.boardlike.repository.BoardLikeRepository;
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
public class BoardLikeEventListener {

    private final BoardLikeRepository boardLikeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    @Transactional
    public void onUserDeleted(UserEvent event) {
        if (event.type() != UserEvent.Type.DELETED) return;

        Long userId = event.userId();
        List<Long> boardIds = boardLikeRepository.findActiveBoardIdsByUserId(userId);

        if (boardIds.isEmpty()) {
            return;
        }

        boardIds.forEach(boardId -> {
            eventPublisher.publishEvent(BoardLikeEvent.deleted(boardId, userId));
        });
    }
}
