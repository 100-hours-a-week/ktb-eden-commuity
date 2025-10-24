package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.domain.Board;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Repository
public class InMemoryBoardRepository implements BoardRepository {

    private final NavigableMap<Long, Board> store = new ConcurrentSkipListMap<>();
    protected final AtomicLong sequence = new AtomicLong(0);

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

    @Override
    public List<Board> findAllByCursor(Long cursorId, int size) {
        if (size <= 0) size = 10;
        NavigableMap<Long, Board> result = (cursorId == null)
                ? store.descendingMap()
                : store.headMap(cursorId, false).descendingMap();

        return result.values().stream()
                .limit(size + 1L)
                .toList();
    }

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            long id = sequence.incrementAndGet();
            board = board.withId(id);
        }
        store.put(board.getId(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Board> findAll() {
        return store.descendingMap().values()
                .stream()
                .toList();
    }

    @Override
    public Optional<Board> update(Long id, UnaryOperator<Board> updater) {
        Board after = store.compute(id, (k, cur) ->
                cur == null ? null : updater.apply(cur)
        );
        return Optional.ofNullable(after);
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
        sequence.set(0);
    }
}
