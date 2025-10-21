package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.global.common.repository.BaseInMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Repository
public class InMemoryBoardRepository extends BaseInMemoryRepository<Board> implements BoardRepository {

    public List<Board> findByAuthorId(Long authorId) {
        return store.values().stream()
                .filter(b -> Objects.equals(b.getAuthorId(), authorId))
                .sorted(Comparator.comparing(Board::getId))
                .toList();
    }

    public List<Board> findAllPaged(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        return store.values().stream()
                .sorted(Comparator.comparing(Board::getId))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    // TODO : 오프셋과 성능 동일 -> NavigableMap OR ConcurrentSkipListMap OR TreeMap 학습해서 개선하기
    @Override
    public List<Board> findAllByCursor(Long cursorId, int size) {
        if (size <= 0) size = 10;

        return store.values().stream()
                .sorted(Comparator.comparing(Board::getId).reversed())
                .filter(b -> cursorId == null || b.getId() < cursorId)
                .limit(size + 1L)
                .toList();
    }

    @Override
    protected Long getId(Board board) {
        return board.getId();
    }

    @Override
    protected Board assignId(Board board, Long id) {
        return board.withId(id);
    }

}
