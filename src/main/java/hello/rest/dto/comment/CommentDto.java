package hello.rest.dto.comment;

import hello.rest.entity.comment.Comment;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long commentId;

    @NotEmpty
    @Size(min = 2, max = 140)
    private String content;

    private Long commentOrderInGroup;

    private Long commentGroupNumber;

    private Long deep;

    private Boolean isSecret;

    private Boolean isDeleted;

    private LocalDate createdDate;

    private Long userId;
    private String username;
    private String email;

    private Long postId;
    private String postUserEmail;

    private List<CommentDto> children = new ArrayList<>();

    public static CommentDto convertCommentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto(comment);
        return commentDto;
    }

    public CommentDto(Long commentId, String content, Long commentOrderInGroup, Long commentGroupNumber, Long deep, Boolean isSecret, Boolean isDeleted, LocalDateTime createdAt, Long userId, String username, String email, Long postId, String postUserEmail) {
        this.commentId = commentId;
        this.content = content;
        this.commentOrderInGroup = commentOrderInGroup;
        this.commentGroupNumber = commentGroupNumber;
        this.deep = deep;
        this.isSecret = isSecret;
        this.isDeleted = isDeleted;
        this.createdDate = createdAt.toLocalDate();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.postId = postId;
        this.postUserEmail = postUserEmail;
    }

    public CommentDto(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.commentOrderInGroup = comment.getCommentOrderInGroup();
        this.commentGroupNumber = comment.getCommentGroupNumber();
        this.deep = comment.getDeep();
        this.isSecret = comment.getIsSecret();
        this.isDeleted = comment.getIsDeleted();
        this.createdDate = comment.getCreatedAt().toLocalDate();
        this.userId = comment.getUser().getId();
        this.username = comment.getUser().getName();
        this.email = comment.getUser().getEmail();
        this.postId = comment.getPost().getPostId();
        this.postUserEmail = comment.getPost().getUser().getEmail();
    }

    public static CommentDto deleteCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto(comment);
        commentDto.setUsername("UnKnown");

        return commentDto;
    }
}
