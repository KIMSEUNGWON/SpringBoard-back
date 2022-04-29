package hello.rest.repository.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.rest.dto.comment.CommentDto;
import hello.rest.dto.comment.CommentSearchCondition;
import hello.rest.entity.comment.Comment;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static hello.rest.dto.comment.CommentDto.convertCommentToDto;
import static hello.rest.entity.QUser.user;
import static hello.rest.entity.board.QPost.post;
import static hello.rest.entity.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDto> findByPost_optimization(CommentSearchCondition condition) {
        return queryFactory.
                select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.content,
                        comment.commentOrderInGroup,
                        comment.commentGroupNumber,
                        comment.deep,
                        comment.isSecret,
                        comment.isDeleted,
                        comment.createdAt,
                        comment.user.id,
                        comment.user.name,
                        comment.user.email,
                        comment.post.postId,
                        comment.post.user.email
                ))
                .from(comment)
                .leftJoin(comment.user, user)
                .leftJoin(comment.post, post)
                .where(postIdEq(condition.getPostId()))
                .fetch();
    }

    @Override
    public List<CommentDto> findAllByPost_orderBy(CommentSearchCondition condition) {
        return queryFactory.
                select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.content,
                        comment.commentOrderInGroup,
                        comment.commentGroupNumber,
                        comment.deep,
                        comment.isSecret,
                        comment.isDeleted,
                        comment.createdAt,
                        comment.user.id,
                        comment.user.name,
                        comment.user.email,
                        comment.post.postId,
                        comment.post.user.email
                ))
                .from(comment)
                .leftJoin(comment.user, user)
                .leftJoin(comment.post, post)
                .where(postIdEq(condition.getPostId()))
                .orderBy(comment.commentGroupNumber.asc(),
                        comment.commentOrderInGroup.asc(),
                        comment.createdAt.asc())
                .fetch();
    }

    @Override
    public List<CommentDto> findAllCommentByPostId(CommentSearchCondition condition) {
        List<Comment> comments = queryFactory.
                selectFrom(comment)
                .leftJoin(comment.user, user)
                .leftJoin(comment.post, post)
                .fetchJoin()
                .where(postIdEq(condition.getPostId()))
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc())
                .fetch();

        List<CommentDto> result = convertNestedStructure(comments);
        return result;
    }

    private List<CommentDto> convertNestedStructure(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();
        comments.stream().forEach(c -> {
            CommentDto commentDto = convertCommentToDto(c);
            map.put(commentDto.getCommentId(), commentDto);
            if (c.getParent() != null) {
                map.get(c.getParent().getId()).getChildren().add(commentDto);
            } else {
                result.add(commentDto);
            }
        });
        return result;
    }

    @Override
    public List<CommentDto> findAllByDto_optimization(CommentSearchCondition condition) {
        List<CommentDto> result = findByPost_optimization(condition);

        condition.setCommentGroupNumbers(toCommentGroupNumbers(result));

        Map<Long, List<CommentDto>> replyMap = findReplyMap(condition);

//        result.forEach(comment -> comment.setReplies(replyMap.get(comment.getCommentId())));

        result.forEach(comment -> {
            comment.getCommentId().equals(replyMap.get(comment.getCommentId()));
        });

        return result;
    }

    public Map<Long, List<CommentDto>> findReplyMap(CommentSearchCondition condition) {

        List<CommentDto> replies = queryFactory.
                select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.content,
                        comment.commentOrderInGroup,
                        comment.commentGroupNumber,
                        comment.deep,
                        comment.isSecret,
                        comment.isDeleted,
                        comment.createdAt,
                        comment.user.id,
                        comment.user.name,
                        comment.user.email,
                        comment.post.postId,
                        comment.post.user.email
                ))
                .from(comment)
                .leftJoin(comment.user, user)
                .leftJoin(comment.post, post)
                .where(postIdEq(condition.getPostId()),
                        commentGroupNumberIn(condition.getCommentGroupNumbers()))
                .fetch();

        return replies.stream()
                .collect(Collectors.groupingBy(CommentDto::getCommentGroupNumber));
    }

    private BooleanExpression commentGroupNumberIn(Set<Long> commentGroupNumbers) {
        return comment.commentGroupNumber.in(commentGroupNumbers);
    }

    private BooleanExpression postIdEq(Long postId) {
        return postId == null ? null : comment.post.postId.eq(postId);
    }

    private Set<Long> toCommentGroupNumbers(List<CommentDto> result) {
        return result.stream()
                .map(n -> n.getCommentGroupNumber())
                .collect(Collectors.toSet());
    }
}
