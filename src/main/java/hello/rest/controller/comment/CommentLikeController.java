package hello.rest.controller.comment;

import hello.rest.dto.comment.CommentLikeDto;
import hello.rest.dto.comment.CommentLikeSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.comment.CommentLike;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.CommentLikeService;
import hello.rest.service.CommentService;
import hello.rest.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/commentLike")
@Slf4j
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final CommentService commentService;
    private final ResponseService responseService;

    @GetMapping("/comment/")
    public ListResult<CommentLikeDto> getAllCommentLikeInComment(@RequestParam Long commentId,
                                                                 @AuthenticationPrincipal User user) throws Exception {

        if (commentId == null || user == null) {
            log.info("commentId={}, user={}", commentId, user);
            throw new Exception();
        }

        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, null);
        List<CommentLikeDto> result = commentLikeService.findAllCommentLikeInComment(condition);

        return responseService.getListResult(result);
    }

    @GetMapping("/comment/user/")
    public ListResult<CommentLikeDto> getAllCommentLikeInCommentByUser(@RequestParam Long commentId,
                                                                       @AuthenticationPrincipal User user) throws Exception {

        if (commentId == null || user == null) {
            log.info("commentId={}, user={}", commentId, user);
            throw new Exception();
        }

        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, user.getId());
        List<CommentLikeDto> result = commentLikeService.findAllCommentLikeInUser(condition);

        return responseService.getListResult(result);
    }

    @GetMapping("/user/")
    public ListResult<CommentLikeDto> getAllCommentLikeInUser(@AuthenticationPrincipal User user) throws Exception {

        if (user == null) {
            throw new Exception();
        }

        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(null, user.getId());
        List<CommentLikeDto> result = commentLikeService.findAllCommentLikeInComment(condition);

        return responseService.getListResult(result);
    }

    @PostMapping("/")
    public SingleResult<CommentLikeDto> likeComment(@RequestParam Long commentId,
                                                    @AuthenticationPrincipal User user) throws Exception {

        if (commentId == null || user == null) {
            throw new Exception();
        }

        Comment targetComment = commentService.findComment(commentId);
        CommentLike commentLike = commentLikeService.makeCommentLike(user, targetComment);
        CommentLikeDto result = new CommentLikeDto(commentLike);

        return responseService.getSingleResult(result);
    }

    @GetMapping("/count/")
    public SingleResult<Long> getAllCommentLikeCountInComment(@RequestParam Long commentId,
                                                              @AuthenticationPrincipal User user) throws Exception {

        if (commentId == null || user == null) {
            log.info("commentId={}, user={}", commentId, user);
            throw new Exception();
        }

        CommentLikeSearchCondition condition = new CommentLikeSearchCondition(commentId, null);
        Long result = commentLikeService.countCommentLikeInComment(condition);

        return responseService.getSingleResult(result);
    }

    // TODO: 2022-04-21 좋아요 제약조건 구현하기
}
