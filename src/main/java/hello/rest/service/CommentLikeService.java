package hello.rest.service;

import hello.rest.dto.comment.CommentLikeDto;
import hello.rest.dto.comment.CommentLikeSearchCondition;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.comment.CommentLike;
import hello.rest.repository.comment.CommentLikeJpaRepository;
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
public class CommentLikeService {

    private final CommentLikeJpaRepository commentLikeJpaRepository;

    public List<CommentLikeDto> findAllCommentLikeInComment(CommentLikeSearchCondition condition) {
        List<CommentLikeDto> commentLikeDtos = commentLikeJpaRepository.findAllCommentLikeByCondition(condition);
        return commentLikeDtos;
    }

    public List<CommentLikeDto> findAllCommentLikeInUser(CommentLikeSearchCondition condition) {
        List<CommentLikeDto> commentLikeDtos = commentLikeJpaRepository.findAllCommentLikeByCondition(condition);
        return commentLikeDtos;
    }

    @LogExecutionTime(type = "Like")
    public CommentLike makeCommentLike(User actionUser, Comment targetComment) {
        CommentLike commentLike =
                commentLikeJpaRepository.save(new CommentLike(actionUser, targetComment));

        return commentLike;
    }

    public Long countCommentLikeInComment(CommentLikeSearchCondition condition) {
        Long result = commentLikeJpaRepository.countCommentLikeInComment(condition);
        return result;
    }
}
