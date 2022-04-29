package hello.rest.service;

import hello.rest.dto.comment.CommentAddDto;
import hello.rest.dto.comment.CommentLikeDto;
import hello.rest.dto.comment.CommentLikeSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.comment.CommentLike;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class CommentLikeServiceTest {

    @Autowired
    private CommentLikeService commentLikeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private PostService postService;

    private User actionUserA, userBIsTargetCommentOwner;
    private Board board;
    private List<Post> posts;
    private CommentAddDto commentAddDto;
    private Comment targetComment;

    @BeforeEach
    public void init() {
        actionUserA = userService.findUser("a@a.com");
        userBIsTargetCommentOwner = userService.findUser("b@b.com");
        board = boardService.findBoard("free");
        posts = postService.findPosts(board.getName());
        commentAddDto = new CommentAddDto("userB's content", false);
        targetComment = commentService.makeComment(userBIsTargetCommentOwner, posts.get(0), commentAddDto);
    }

    @Test
    public void makeCommentLikeTest() throws Exception {
        //given
        //when
        CommentLike commentLike = commentLikeService.makeCommentLike(actionUserA, targetComment);

        //then
        assertThat(commentLike.getActionUser()).isEqualTo(actionUserA);
        assertThat(commentLike.getTargetComment()).isEqualTo(targetComment);
    }

    @Test
    @Rollback(false)
    public void findAllCommentLikeInUserTest() throws Exception {
        //given
        Long commentId = targetComment.getId();
        Long userId = actionUserA.getId();
        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, userId);

        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);

        commentService.makeComment(actionUserA, posts.get(0), commentAddDto);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);

        //when
        List<CommentLikeDto> userACommentLike = commentLikeService.findAllCommentLikeInComment(condition);

        //then
        assertThat(userACommentLike.size()).isEqualTo(4);
        for (CommentLikeDto commentLikeDto : userACommentLike) {
            System.out.println("commentLikeDto = " + commentLikeDto);
        }
    }

    @Test
    @Rollback(false)
    public void findAllCommentLikeInCommentTest() throws Exception {
        //given
        Long commentId = targetComment.getId();
        Long userId = actionUserA.getId();
        CommentLikeSearchCondition allCondition = new CommentLikeSearchCondition(commentId, null);
        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, userId);

        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);

        commentService.makeComment(actionUserA, posts.get(0), commentAddDto);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);

        //when
        List<CommentLikeDto> all = commentLikeService.findAllCommentLikeInComment(allCondition);
        List<CommentLikeDto> userACommentLike = commentLikeService.findAllCommentLikeInComment(condition);

        //then
        assertThat(all.size()).isEqualTo(8);
        assertThat(userACommentLike.size()).isEqualTo(4);
        for (CommentLikeDto commentLikeDto : all) {
            System.out.println("commentLikeDto = " + commentLikeDto);
        }
        for (CommentLikeDto commentLikeDto : userACommentLike) {
            System.out.println("commentLikeDto = " + commentLikeDto);
        }
    }

    @Test
    @Rollback(false)
    public void countCommentLikeInCommentTest() throws Exception {
        //given
        Long commentId = targetComment.getId();
        Long userId = actionUserA.getId();
        CommentLikeSearchCondition allCondition = new CommentLikeSearchCondition(commentId, null);
        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, userId);

        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);
        commentLikeService.makeCommentLike(actionUserA, targetComment);

        commentService.makeComment(actionUserA, posts.get(0), commentAddDto);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);
        commentLikeService.makeCommentLike(userBIsTargetCommentOwner, targetComment);

        //when
        Long allCommentLikeCountInComment = commentLikeService.countCommentLikeInComment(allCondition);
        Long userACommentLikeCountInComment = commentLikeService.countCommentLikeInComment(condition);

        //then
        assertThat(allCommentLikeCountInComment).isEqualTo(8L);
        assertThat(userACommentLikeCountInComment).isEqualTo(4L);
    }
}