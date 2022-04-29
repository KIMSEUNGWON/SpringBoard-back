package hello.rest.service;

import hello.rest.advice.exception.CBoardAlreadyExistedException;
import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.entity.board.Board;
import hello.rest.repository.BoardJpaRepository;
import hello.rest.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardJpaRepository boardJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final UserService userService;

    public Board findBoard(String boardName) {
        return boardJpaRepository.findByName(boardName)
                .orElseThrow(CResourceNotExistException::new);
    }

    public Board insertBoard(String boardName) {

        Optional<Board> board = boardJpaRepository.findByName(boardName);
        if (board.isPresent()) {
            throw new CBoardAlreadyExistedException();
        }

        return boardJpaRepository.save(
                Board.builder()
                        .name(boardName)
                        .build()
        );
    }

    public List<Board> findBoardList() {
        return boardJpaRepository.findAll();
    }
}
