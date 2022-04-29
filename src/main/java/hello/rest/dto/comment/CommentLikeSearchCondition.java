package hello.rest.dto.comment;

import lombok.Data;

@Data
public class CommentLikeSearchCondition {

    private Long commentId;
    private Long userId;

    public CommentLikeSearchCondition(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }
}
