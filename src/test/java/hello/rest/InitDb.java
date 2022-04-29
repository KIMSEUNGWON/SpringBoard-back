package hello.rest;

import hello.rest.advice.exception.CUserNotFoundException;
import hello.rest.dto.BoardDto;
import hello.rest.dto.comment.CommentAddDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.repository.BoardJpaRepository;
import hello.rest.repository.PostJpaRepository;
import hello.rest.repository.UserJpaRepository;
import hello.rest.service.BoardService;
import hello.rest.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Profile("test")
@Component
@RequiredArgsConstructor
@Slf4j
//https://kukekyakya.tistory.com/532
public class InitDb {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardJpaRepository boardJpaRepository;
    private final BoardService boardService;
    private final PostJpaRepository postJpaRepository;
    private final CommentService commentService;

    @PostConstruct
    public void InitDb() {
        log.info("initialize database");
        initTestUser();
        initTestBoard();
        initTestPost();
//        initTestCommentAndReply();
    }

    private final static String freeBoard = "free";

    private void initTestUser() {
        userJpaRepository.saveAll(
                List.of(
                        User.builder()
                                .email("a@a.com")
                                .password(passwordEncoder.encode("123"))
                                .name("a").roles(Collections.singletonList("ROLE_USER"))
                                .build(),
                        User.builder()
                                .email("b@b.com")
                                .password(passwordEncoder.encode("123"))
                                .name("b").roles(Collections.singletonList("ROLE_USER"))
                                .build()
                )
        );
    }

    private void initTestBoard() {
//        boardJpaRepository.save(Board.builder().name("pokemon").build());
        boardJpaRepository.save(Board.builder().name(freeBoard).build());

        for (int i = 2; i < 20; i++) {
            boardJpaRepository.save(Board.builder().name("board " + (i + 1)).build());
        }
    }

    private void initTestPost() {
        for (int i = 0; i < 20; i++) {
            BoardDto boardDto =
                    new BoardDto("author " + (i + 1), "title " + (i + 1), "content " + (i + 1));
            Board board = boardService.findBoard(freeBoard);
            Post post = new Post(
                    userJpaRepository.findByEmail("a@a.com").orElseThrow(CUserNotFoundException::new),
                    board, boardDto.getAuthor(), boardDto.getTitle(), boardDto.getContent()
            );

            postJpaRepository.save(post);
        }
    }

    private void initTestCommentAndReply() {
        User userA = userJpaRepository.findByEmail("a@a.com").orElseThrow(CUserNotFoundException::new);
        User userB = userJpaRepository.findByEmail("b@b.com").orElseThrow(CUserNotFoundException::new);
        Board board = boardService.findBoard("free");
        List<Post> posts = postJpaRepository.findByBoard(board);

        for (int i = 0; i < 10; i++) {
            CommentAddDto commentAddDto = new CommentAddDto("test" + i, false);
            Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);
        }

        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        for (int i = 0; i < 10; i++) {
            CommentAddDto childCommentAddDto = new CommentAddDto("childContent" + i, false);
            Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);
            if (i == 9) {
                for (int j = 0; j < 5; j++) {
                    CommentAddDto childCommentAddDto2 = new CommentAddDto("child" + j, false);
                    Comment reply = commentService.makeReply(userB, childComment, childCommentAddDto2);
                }
            }
        }
    }
}
