package hello.rest.service;

import hello.rest.dto.comment.CommentAddDto;
import hello.rest.dto.comment.CommentDto;
import hello.rest.dto.comment.CommentEditDto;
import hello.rest.dto.comment.CommentSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
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
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private PostService postService;

    private User userA, userB;
    private Board board;
    private List<Post> posts;

    @BeforeEach
    public void init() {
        userA = userService.findUser("a@a.com");
        userB = userService.findUser("b@b.com");
        board = boardService.findBoard("free");
        posts = postService.findPosts(board.getName());
    }

    @Test
    public void makeCommentTest() throws Exception {
        //given
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);

        //when
        Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);

        //then
        assertThat(comment.getContent()).isEqualTo(commentAddDto.getContent());
        assertThat(comment.getCommentOrderInGroup()).isEqualTo(1L);
        assertThat(comment.getCommentGroupNumber()).isEqualTo(comment.getId());
        assertThat(comment.getDeep()).isEqualTo(0);
        assertThat(comment.getIsSecret()).isEqualTo(commentAddDto.getIsSecret());
        assertThat(comment.getUser().getEmail()).isEqualTo(userA.getEmail());
        assertThat(comment.getPost().getUser().getName()).isEqualTo(posts.get(0).getUser().getName());

    }

    @Test
    public void makeReplyTest() throws Exception {
        //given
        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        CommentAddDto childCommentAddDto = new CommentAddDto("childContent", false);

        //when
        Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);

        //then
        assertThat(parentComment.getCommentOrderInGroup()).isEqualTo(1L);
        assertThat(parentComment.getCommentGroupNumber()).isEqualTo(parentComment.getId());
        assertThat(childComment.getContent()).isEqualTo(childCommentAddDto.getContent());
        assertThat(childComment.getCommentOrderInGroup()).isEqualTo(parentComment.getCommentOrderInGroup() + 1L);
        assertThat(childComment.getCommentGroupNumber()).isEqualTo(parentComment.getCommentGroupNumber());
        assertThat(childComment.getDeep()).isEqualTo(parentComment.getDeep() + 1);
        assertThat(childComment.getIsSecret()).isEqualTo(childCommentAddDto.getIsSecret());
        assertThat(childComment.getUser()).isEqualTo(userB);
        assertThat(childComment.getPost()).isEqualTo(posts.get(0));
    }

    @Test
    public void findCommentTest() throws Exception {
        //given
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);
        Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);

        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        CommentAddDto childCommentAddDto = new CommentAddDto("childContent", false);
        Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);

        //when
        Comment findChildComment = commentService.findComment(childComment.getId());

        //then
        assertThat(findChildComment).isEqualTo(childComment);

    }

    @Test
    public void findCommentsByPostTest() throws Exception {
        //given
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);
        Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);

        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        CommentAddDto childCommentAddDto = new CommentAddDto("childContent", false);
        Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);

        //when
        List<Comment> commentsByPost = commentService.findCommentsByPost(posts.get(0));

        //then
        assertThat(commentsByPost.size()).isEqualTo(3);
        assertThat(commentsByPost)
                .extracting("post")
                .containsExactly(posts.get(0), posts.get(0), posts.get(0));
        assertThat(commentsByPost)
                .extracting("user")
                .containsExactly(userA, userB, userB);
        assertThat(commentsByPost)
                .extracting("content")
                .containsExactly(comment.getContent(), parentComment.getContent(), childComment.getContent());
        assertThat(commentsByPost)
                .extracting("commentOrderInGroup")
                .containsExactly(comment.getCommentOrderInGroup(), parentComment.getCommentOrderInGroup(), parentComment.getCommentOrderInGroup() + 1L);
        assertThat(commentsByPost)
                .extracting("commentGroupNumber")
                .containsExactly(comment.getCommentGroupNumber(), parentComment.getCommentGroupNumber(), parentComment.getCommentGroupNumber());
        assertThat(commentsByPost)
                .extracting("deep")
                .containsExactly(0L, 0L, 1L);
        assertThat(commentsByPost)
                .extracting("isSecret")
                .containsExactly(false, false, false);
    }

    @Test
    public void editCommentTest() throws Exception {
        //given
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);
        Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);

        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        CommentAddDto childCommentAddDto = new CommentAddDto("childContent", false);
        Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);

        CommentEditDto commentEditDto = new CommentEditDto("editContent", true);

        //when
        Comment editComment = commentService.editComment(childComment.getId(), commentEditDto);

        //then
        assertThat(editComment.getContent()).isEqualTo(commentEditDto.getContent());
        assertThat(editComment.getCommentOrderInGroup()).isEqualTo(childComment.getCommentOrderInGroup());
        assertThat(editComment.getCommentGroupNumber()).isEqualTo(childComment.getCommentGroupNumber());
        assertThat(editComment.getDeep()).isEqualTo(childComment.getDeep());
        assertThat(editComment.getIsSecret()).isEqualTo(commentEditDto.getIsSecret());
        assertThat(editComment.getUser()).isEqualTo(childComment.getUser());
        assertThat(editComment.getPost()).isEqualTo(childComment.getPost());

    }

    @Test
    public void deleteCommentTest() throws Exception {
        //given
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);
        Comment comment = commentService.makeComment(userA, posts.get(0), commentAddDto);

        CommentAddDto parentCommentAddDto = new CommentAddDto("parentContent", false);
        Comment parentComment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        CommentAddDto childCommentAddDto = new CommentAddDto("childContent", false);
        Comment childComment = commentService.makeReply(userB, parentComment, childCommentAddDto);

        //when
        Comment deleteComment = commentService.deleteComment(childComment.getId());

        //then
        assertThat(deleteComment).isEqualTo(childComment);
        assertThat(deleteComment.getContent()).isEqualTo("content is deleted");
    }

    @Test
    public void findCommentsByPost_optimizationTest() throws Exception {
        //given
        for (int i = 10; i < 20; i++) {
            CommentAddDto commentAddDto = new CommentAddDto("test" + i, false);
            Comment comment = commentService.makeComment(userA, posts.get(1), commentAddDto);
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

        CommentSearchCondition condition = new CommentSearchCondition(posts.get(0).getPostId());

        //when
        List<CommentDto> result = commentService.findCommentsByPost_optimization(condition);

        //then
        for (CommentDto commentDto : result) {
            System.out.println("commentDto = " + commentDto);
        }
    }

    @Test
    public void findAllByDto_optimizationTest() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
//            CommentAddDto commentAddDto = new CommentAddDto("test" + i, false);
//            Comment comment = commentService.makeComment(userA, posts.get(1), commentAddDto);

//            commentService.makeComment(userA, posts.get(0), commentAddDto);
        }

        CommentAddDto parentCommentAddDto = new CommentAddDto("parent", false);
        Comment comment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        for (int i = 0; i < 10; i++) {
            CommentAddDto childCommentAddDto = new CommentAddDto("child" + i, false);
            Comment reply = commentService.makeReply(userB, comment, childCommentAddDto);
            if (i == 9) {
                for (int j = 0; j < 5; j++) {
                    CommentAddDto childCommentAddDto2 = new CommentAddDto("childChild" + j, false);
                    Comment replyReply = commentService.makeReply(userB, reply, childCommentAddDto2);
                }
            }
        }

        CommentSearchCondition condition = new CommentSearchCondition(posts.get(0).getPostId());

        //when
        List<CommentDto> result = commentService.findAllByDto_optimization(condition);

        //then
        for (CommentDto commentDto : result) {
            System.out.println("commentDto = " + commentDto);
        }
    }

    @Test
    public void findAllCommentByPostIdTest() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
//            CommentAddDto commentAddDto = new CommentAddDto("test" + i, false);
//            Comment comment = commentService.makeComment(userA, posts.get(1), commentAddDto);

//            commentService.makeComment(userA, posts.get(0), commentAddDto);
        }

        CommentAddDto parentCommentAddDto = new CommentAddDto("parent", false);
        Comment comment = commentService.makeComment(userB, posts.get(0), parentCommentAddDto);
        for (int i = 0; i < 10; i++) {
            CommentAddDto childCommentAddDto = new CommentAddDto("child" + i, false);
            Comment reply = commentService.makeReply(userB, comment, childCommentAddDto);
            if (i == 9) {
                for (int j = 0; j < 5; j++) {
                    CommentAddDto childCommentAddDto2 = new CommentAddDto("childChild" + j, false);
                    Comment replyReply = commentService.makeReply(userB, reply, childCommentAddDto2);
                }
            }
        }

        CommentSearchCondition condition = new CommentSearchCondition(posts.get(0).getPostId());

        //when
        List<CommentDto> result = commentService.findAllCommentByPostId(condition);

        //then
        for (CommentDto commentDto : result) {
            System.out.println("commentDto = " + commentDto);
        }
    }
}