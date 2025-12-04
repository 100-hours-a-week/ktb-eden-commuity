package com.example.restapi_subject.domain.comment.repository;

import com.example.restapi_subject.domain.comment.domain.Comment;
import com.example.restapi_subject.global.common.repository.CrudCustomRepository;

import java.util.List;

public interface CommentRepository extends CrudCustomRepository<Comment, Long> {

    public List<Comment> findByBoardIdPaged(Long boardId, int page, int size);
    public int countByBoardId(Long boardId);
}