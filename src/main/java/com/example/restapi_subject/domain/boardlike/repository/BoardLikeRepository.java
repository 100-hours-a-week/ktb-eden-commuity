package com.example.restapi_subject.domain.boardlike.repository;

import java.util.List;
import java.util.Set;

public interface BoardLikeRepository {

    boolean add(Long boardId, Long userId);
    boolean remove(Long boardId, Long userId);
    boolean exists(Long boardId, Long userId);
    int countByBoardId(Long boardId);
    Set<Long> findAllByUserIdAndBoardIds(List<Long> boardIds, Long userId);

}
