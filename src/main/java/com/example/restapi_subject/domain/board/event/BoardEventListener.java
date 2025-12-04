package com.example.restapi_subject.domain.board.event;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.boardlike.event.BoardLikeEvent;
import com.example.restapi_subject.domain.comment.event.CommentEvent;
import com.example.restapi_subject.domain.user.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardEventListener {

    /**
     * TODO : 비동기 방식인데 동시성 고려 해야할 부분이 있는지? 삽입/삭제 트랜잭션
     */
    private final BoardRepository boardRepository;

//    @Async
//    @EventListener
//    @Transactional
//    public void onCommentEvent(CommentEvent event) {
//        int delta = switch (event.type()) {
//            case CREATED -> 1;
//            case DELETED -> -1;
//        };
//        boardRepository.updateCommentCount(event.boardId(), delta);
//    }
//
//    @Async
//    @EventListener
//    @Transactional
//    public void onBoardLikeEvent(BoardLikeEvent event) {
//        int delta = switch (event.type()) {
//            case CREATED -> 1;
//            case DELETED -> -1;
//        };
//        boardRepository.updateLikeCount(event.boardId(), delta);
//    }

    @Async
    @EventListener
    @Transactional
    public void onUserEvent(UserEvent event) {
        switch (event.type()) {
            case DELETED ->  {
                boardRepository.softDeleteByUserId(event.userId());
            }
        }
    }
}

