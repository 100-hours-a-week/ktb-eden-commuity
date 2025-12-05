package com.example.restapi_subject.domain.boardlike.service;

import com.example.restapi_subject.domain.board.domain.Board;
import com.example.restapi_subject.domain.board.repository.BoardRepository;
import com.example.restapi_subject.domain.board.service.BoardValidator;
import com.example.restapi_subject.domain.boardlike.dto.BoardLikeResDto;
import com.example.restapi_subject.domain.boardlike.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardLikeService {

    private final BoardRepository boardRepository;
    private final BoardValidator boardValidator;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public BoardLikeResDto.LikeDto like(Long boardId, Long userId) {
        boardValidator.ensureBoardExists(boardId);

        boolean added = boardLikeRepository.add(boardId, userId);
        if (!added) return BoardLikeResDto.LikeDto.of(boardId, boardLikeRepository.countByBoardId(boardId), true);

        boardRepository.updateLikeCount(boardId, +1);
        int likeCount = boardLikeRepository.countByBoardId(boardId);
        return BoardLikeResDto.LikeDto.of(boardId, likeCount, true);
    }

    @Transactional
    public BoardLikeResDto.LikeDto unlike(Long boardId, Long userId) {
        boardValidator.ensureBoardExists(boardId);

        boolean removed = boardLikeRepository.remove(boardId, userId);
        if (!removed) return BoardLikeResDto.LikeDto.of(boardId, boardLikeRepository.countByBoardId(boardId), false);

        boardRepository.updateLikeCount(boardId, -1);
        int likeCount = boardLikeRepository.countByBoardId(boardId);
        return BoardLikeResDto.LikeDto.of(boardId, likeCount, false);
    }

    public boolean isLiked(Long boardId, Long userId) {
        return boardLikeRepository.exists(boardId, userId);
    }

    public Set<Long> getLikedBoardIds(List<Board> boards, Long userIdOrNull) {
        if(boards.isEmpty() || userIdOrNull == null) return Set.of();
        List<Long> boardIds = boards.stream()
                .map(Board::getId)
                .toList();
        return boardLikeRepository.findAllByUserIdAndBoardIds(boardIds, userIdOrNull);
    }
}
