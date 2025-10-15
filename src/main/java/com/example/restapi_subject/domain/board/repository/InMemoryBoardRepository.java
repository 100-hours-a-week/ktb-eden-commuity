package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Repository
public class InMemoryBoardRepository {

    private final Map<Long, Board> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public Board save(Board board) {
        if (board.getId() == null) board = board.withId(seq.incrementAndGet());
        store.put(board.getId(), board);
        return board;
    }

    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Board> findByAuthorId(Long authorId) {
        return store.values().stream()
                .filter(b -> Objects.equals(b.getAuthorId(), authorId))
                .sorted(Comparator.comparing(Board::getId))
                .toList();
    }

    public List<Board> findAll() {
        return store.values().stream()
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

    public List<Board> findAllByCursor(Long cursorId, int size) {
        if (size <= 0) size = 10;

        return store.values().stream()
                .sorted(Comparator.comparing(Board::getId).reversed())
                .filter(b -> cursorId == null || b.getId() < cursorId)
                .limit(size + 1L)
                .toList();
    }


    public Optional<Board> update(Long id, UnaryOperator<Board> updater) {
        Board after = store.compute(id, (k, cur) ->
                cur == null ? null : updater.apply(cur)
        );
        return Optional.ofNullable(after);
    }

    public void delete(Long id) { store.remove(id); }
    public void clear() { store.clear(); seq.set(0); }
}
