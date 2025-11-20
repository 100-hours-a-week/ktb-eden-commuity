package com.example.restapi_subject.domain.board.event;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.boardlike.event.BoardLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardEventListener {

    /**
     * TODO : 비동기 방식인데 동시성 고려 해야할 부분이 있는지? 삽입/삭제 트랜잭션
     */
    private final BoardRepository boardRepository;

    @Async
    @EventListener
    @Transactional
    public void onCommentEvent(CommentEvent event) {
        boardRepository.update(event.boardId(), b -> {
            switch (event.type()) {
                case CREATED -> b.increaseComment();
                case DELETED -> b.decreaseComment();
            }
            return b;
        });
    }

    @Async
    @EventListener
    @Transactional
    public void onBoardLikeEvent(BoardLikeEvent event) {
        boardRepository.update(event.boardId(), b -> {
            switch (event.type()) {
                case CREATED -> b.increaseLike();
                case DELETED -> b.decreaseLike();
            }
            return b;
        });
    }
}

