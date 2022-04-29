package hello.rest.dto.comment;

import hello.rest.entity.comment.CommentLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeDto {

    private Long commentLikeId;

    private Long actionUserId;
    private String actionUsername;
    private String actionUserEmail;

    private Long targetCommentId;
    private Long targetCommentUserId;
    private String targetCommentUsername;
    private String targetCommentUserEmail;

    public static CommentLikeDto convertCommentLikeToDto(CommentLike commentLike) {
        CommentLikeDto commentDto = new CommentLikeDto(commentLike);
        return commentDto;
    }

    public CommentLikeDto(CommentLike commentLike) {
        this.commentLikeId = commentLike.getId();
        this.actionUserId = commentLike.getActionUser().getId();
        this.actionUsername = commentLike.getActionUser().getUsername();
        this.actionUserEmail = commentLike.getActionUser().getEmail();
        this.targetCommentId = commentLike.getTargetComment().getId();
        this.targetCommentUserId = commentLike.getTargetComment().getUser().getId();
        this.targetCommentUsername = commentLike.getTargetComment().getUser().getUsername();
        this.targetCommentUserEmail = commentLike.getTargetComment().getUser().getEmail();
    }
}
