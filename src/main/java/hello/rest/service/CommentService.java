package hello.rest.service;

import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.dto.comment.CommentAddDto;
import hello.rest.dto.comment.CommentDto;
import hello.rest.dto.comment.CommentEditDto;
import hello.rest.dto.comment.CommentSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.repository.comment.CommentJpaRepository;
import hello.rest.service.notification.aspect.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentJpaRepository commentJpaRepository;

    public Comment findComment(Long commentId) {
        return commentJpaRepository
                .findById(commentId).orElseThrow(CResourceNotExistException::new);
    }

    public List<Comment> findCommentsByPost(Post post) {
        return commentJpaRepository.findByPost(post);
    }

    public List<CommentDto> findCommentsByPost_optimization(CommentSearchCondition condition) {
        return commentJpaRepository.findByPost_optimization(condition);
    }

    public List<CommentDto> findAllByDto_optimization(CommentSearchCondition condition) {
        return commentJpaRepository.findAllByDto_optimization(condition);
    }

    public List<CommentDto> findAllByPost_orderBy(CommentSearchCondition condition) {
        return commentJpaRepository.findAllByPost_orderBy(condition);
    }

    public List<CommentDto> findAllCommentByPostId(CommentSearchCondition condition) {
        List<CommentDto> comments = commentJpaRepository.findAllCommentByPostId(condition);
        return comments;
    }

    @LogExecutionTime(type = "comment")
    public Comment makeComment(User user, Post post, CommentAddDto commentAddDto) {
        Comment comment = commentJpaRepository
                .save(Comment.makeComment(user, post, commentAddDto));
        comment.setCommentGroupNumber(comment.getId());

        return comment;
    }

    // 계층형 댓글 모양 https://pulpul8282.tistory.com/170
    // https://daspace.tistory.com/272
    // 정렬 쿼리: SELECT * FROM COMMENT order by  Comment.comment_group_number, comment.comment_order_in_group
    @LogExecutionTime(type = "reply")
    public Comment makeReply(User user, Comment parentComment, CommentAddDto commentAddDto) {
        Comment comment = commentJpaRepository
                .save(Comment.makeReply(user, parentComment, commentAddDto));

        return comment;
    }

    public Comment editComment(Long commentId, CommentEditDto commentEditDto) {
        Comment comment = findComment(commentId);
        comment.editComment(commentEditDto);

        return comment;
    }

    public Comment deleteComment(Long commentId) {
        Comment comment = findComment(commentId);
//        commentJpaRepository.delete(comment);
        comment.delete();

        return comment;
    }
}
