package hello.rest.repository.comment;

import hello.rest.dto.comment.CommentDto;
import hello.rest.dto.comment.CommentSearchCondition;

import java.util.List;

public interface CommentRepositoryCustom {

    //    List<CommentDto> search(CommentSearchCondition condition);
    List<CommentDto> findByPost_optimization(CommentSearchCondition condition);

    List<CommentDto> findAllByPost_orderBy(CommentSearchCondition condition);

    List<CommentDto> findAllCommentByPostId(CommentSearchCondition condition);

    List<CommentDto> findAllByDto_optimization(CommentSearchCondition condition);
}
