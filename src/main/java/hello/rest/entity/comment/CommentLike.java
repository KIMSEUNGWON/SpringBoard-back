package hello.rest.entity.comment;

import hello.rest.entity.Noticeable;
import hello.rest.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentLike implements Noticeable {

    @Id
    @GeneratedValue
    @Column(name = "comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_user_id")
    private User actionUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_comment_id")
    private Comment targetComment;

    public CommentLike(User actionUser, Comment targetComment) {
        this.actionUser = actionUser;
        this.targetComment = targetComment;
    }

    @Override
    public Long getTargetObjectId() {
        return id;
    }

    @Override
    public Long getTargetUserId() {
        return targetComment.getUser().getId();
    }

    @Override
    public String getFormatMessage(String actionUserName, String format) {
        return "내 댓글에 " + actionUserName + "님이 " + format;
    }

    @Override
    public String getFormatUrl() {
        String boardName = targetComment.getPost().getBoard().getName();
        Long postId = targetComment.getPost().getPostId();
        return "/board/" + boardName + "/post/" + postId;
    }

    @Override
    public String getFormat() {
        return "좋아요를 눌렀습니다.";
    }
}
