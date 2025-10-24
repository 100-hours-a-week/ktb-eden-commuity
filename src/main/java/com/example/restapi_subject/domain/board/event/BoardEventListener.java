package com.example.restapi_subject.domain.board.event;

import com.example.restapi_subject.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardEventListener {

    //TODO : Update도 필요한지??
    private final BoardRepository boardRepository;

    @EventListener
    public void onCommentEvent(CommentEvent event) {
        if (event.type() == CommentEvent.Type.CREATED) {
            boardRepository.update(event.boardId(), b -> {
                b.increaseComment();
                return b;
            });
        } else boardRepository.update(event.boardId(), b -> {
            b.decreaseComment();
            return b;
        });
    }
}

