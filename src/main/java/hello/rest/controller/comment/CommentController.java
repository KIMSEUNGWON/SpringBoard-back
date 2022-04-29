package hello.rest.controller.comment;

import hello.rest.dto.comment.CommentAddDto;
import hello.rest.dto.comment.CommentDto;
import hello.rest.dto.comment.CommentEditDto;
import hello.rest.dto.comment.CommentSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.CommentService;
import hello.rest.service.PostService;
import hello.rest.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final ResponseService responseService;

    @GetMapping("/")
    public ListResult<CommentDto> getComments(@RequestParam Long postId,
                                              @AuthenticationPrincipal User user) throws Exception {

        if (postId == null || user == null) {
            log.info("postId={}, user={}", postId, user);
            throw new Exception();
        }

        CommentSearchCondition condition = new CommentSearchCondition(postId);
//        List<CommentDto> result = commentService.findAllByDto_optimization(condition);
//        List<CommentDto> result = commentService.findCommentsByPost_optimization(condition);
//        List<CommentDto> result = commentService.findAllByPost_orderBy(condition);
        List<CommentDto> result = commentService.findAllCommentByPostId(condition);

        return responseService.getListResult(result);
    }

    @PostMapping("/")
    public SingleResult<CommentDto> addComment(@RequestParam Long postId,
                                               @RequestBody CommentAddDto commentAddDto,
                                               @AuthenticationPrincipal User user) throws Exception {

        if (postId == null || user == null || commentAddDto == null) {
            throw new Exception();
        }

        Post post = postService.getPost(postId);
        Comment addComment = commentService.makeComment(user, post, commentAddDto);
        CommentDto result = new CommentDto(addComment);

        return responseService.getSingleResult(result);
    }

    @PostMapping("/reply/")
    public SingleResult<CommentDto> addReply(@RequestParam Long commentId,
                                             @RequestBody CommentAddDto commentAddDto,
                                             @AuthenticationPrincipal User user) throws Exception {

        if (commentId == null || user == null || commentAddDto == null) {
            throw new Exception();
        }

        Comment parentComment = commentService.findComment(commentId);
        Comment reply = commentService.makeReply(user, parentComment, commentAddDto);
        CommentDto result = new CommentDto(reply);

        return responseService.getSingleResult(result);
    }

    // TODO: 2022-04-11 댓글, 알림 같이 생성되게 수정하기

    //    @PreAuthorize("hasPermission('comment', 'edit')")
    @PreAuthorize("hasPermission(#commentId, 'comment', 'edit')")
    @PutMapping("/")
    public SingleResult<CommentDto> editComment(@RequestParam Long commentId,
                                                @RequestBody CommentEditDto commentEditDto) throws Exception {

        if (commentId == null || commentEditDto == null) {
            throw new Exception();
        }

        Comment editComment = commentService.editComment(commentId, commentEditDto);
        CommentDto result = new CommentDto(editComment);

        return responseService.getSingleResult(result);
    }

    @PreAuthorize("hasPermission(#commentId, 'comment', 'delete')")
    @DeleteMapping("/")
    public SingleResult<CommentDto> deleteComment(@RequestParam Long commentId) throws Exception {

        if (commentId == null) {
            throw new Exception();
        }

        Comment deleteComment = commentService.deleteComment(commentId);
        CommentDto result = CommentDto.deleteCommentDto(deleteComment);

        return responseService.getSingleResult(result);
    }
}
