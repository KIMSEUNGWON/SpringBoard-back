package hello.rest.repository.comment;

import hello.rest.dto.comment.CommentLikeDto;
import hello.rest.dto.comment.CommentLikeSearchCondition;

import java.util.List;

public interface CommentLikeRepositoryCustom {

    List<CommentLikeDto> findAllCommentLikeByCondition(CommentLikeSearchCondition condition);

    Long countCommentLikeInComment(CommentLikeSearchCondition condition);
}
