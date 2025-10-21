package com.example.restapi_subject.domain.board.repository;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.global.common.repository.CrudCustomRepository;

import java.util.List;

public interface BoardRepository extends CrudCustomRepository<Board, Long> {

    public List<Board> findAllByCursor(Long cursorId, int size);

}