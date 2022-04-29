package hello.rest.repository.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.rest.dto.comment.CommentLikeDto;
import hello.rest.dto.comment.CommentLikeSearchCondition;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static hello.rest.entity.QUser.user;
import static hello.rest.entity.comment.QComment.comment;
import static hello.rest.entity.comment.QCommentLike.commentLike;

@RequiredArgsConstructor
public class CommentLikeRepositoryCustomImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentLikeDto> findAllCommentLikeByCondition(CommentLikeSearchCondition condition) {
        return queryFactory
                .select(Projections.constructor(CommentLikeDto.class,
                        commentLike.id,
                        commentLike.actionUser.id,
                        commentLike.actionUser.name,
                        commentLike.actionUser.email,
                        commentLike.targetComment.id,
                        commentLike.targetComment.user.id,
                        commentLike.targetComment.user.name,
                        commentLike.targetComment.user.email
                ))
                .from(commentLike)
                .leftJoin(commentLike.actionUser, user)
                .leftJoin(commentLike.targetComment, comment)
                .where(userIdEq(condition.getUserId()),
                        commentIdEq(condition.getCommentId()))
                .fetch();
    }

    @Override
    public Long countCommentLikeInComment(CommentLikeSearchCondition condition) {
        List<Long> result = queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .leftJoin(commentLike.actionUser, user)
                .leftJoin(commentLike.targetComment, comment)
                .where(userIdEq(condition.getUserId()),
                        commentIdEq(condition.getCommentId()))
                .fetch();

        return result.get(0);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : user.id.eq(userId);
    }

    private BooleanExpression commentIdEq(Long commentId) {
        return commentId == null ? null : comment.id.eq(commentId);
    }
}
