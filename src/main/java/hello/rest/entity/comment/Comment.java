package hello.rest.entity.comment;

import hello.rest.dto.comment.CommentAddDto;
import hello.rest.dto.comment.CommentEditDto;
import hello.rest.entity.Noticeable;
import hello.rest.entity.User;
import hello.rest.entity.board.Post;
import hello.rest.entity.common.CommonDateEntity;
import hello.rest.entity.notification.Notification;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends CommonDateEntity implements Noticeable {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 140)
    private String content;

    @Column
    private Long commentOrderInGroup;

    @Column
    private Long commentGroupNumber;

    @Column
    private Long deep;

    @Column
    private Boolean isSecret;

    @Column
    private Boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "targetComment", orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToOne(fetch = LAZY, orphanRemoval = true)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    // 계층형 댓글 구조 https://daspace.tistory.com/272
    // 계층형 댓글 구조 https://pulpul8282.tistory.com/170
    // 계층형 댓글 구조 https://www.a-mean-blog.com/ko/blog/Node-JS-%EC%B2%AB%EA%B1%B8%EC%9D%8C/%EA%B2%8C%EC%8B%9C%ED%8C%90-%EB%A7%8C%EB%93%A4%EA%B8%B0-%EA%B3%A0%EA%B8%89/%EA%B2%8C%EC%8B%9C%ED%8C%90-%EB%8C%93%EA%B8%80-%EA%B8%B0%EB%8A%A5-%EB%A7%8C%EB%93%A4%EA%B8%B0-3-%EB%8C%80%EB%8C%93%EA%B8%80-%EA%B8%B0%EB%8A%A5
    // 계층형 댓글 구조 https://velog.io/@mowinckel/%EC%BD%94%EB%94%A9-%ED%95%99%EC%9B%90-%EA%B4%91%EA%B3%A0%EC%99%80-%EB%B9%84%EC%A0%84%EA%B3%B5-%EA%B0%9C%EB%B0%9C%EC%9E%90
    // 계층형 댓글 구조 https://pro-dev.tistory.com/37
    // 계층형 댓글 구조 https://xerar.tistory.com/44
    //
    // 계층형 댓글 무한 대댓글 기능 https://kukekyakya.tistory.com/9
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public static Comment makeComment(User user, Post post, CommentAddDto commentAddDto) {
        return new Comment(user, post, commentAddDto);
    }

    public Comment(User user, Post post, CommentAddDto commentAddDto) {
        this.content = commentAddDto.getContent();
        this.commentOrderInGroup = 1L;
        this.commentGroupNumber = -1L;
        this.deep = 0L;
        this.isSecret = commentAddDto.getIsSecret();
        this.isDeleted = false;
        this.user = user;
        this.post = post;
        this.parent = null;
    }

    public static Comment makeReply(User user, Comment parentComment, CommentAddDto commentAddDto) {
        return new Comment(user, parentComment, commentAddDto);
    }

    public Comment(User user, Comment parentComment, CommentAddDto commentAddDto) {
        this.content = commentAddDto.getContent();
        this.commentOrderInGroup = parentComment.getCommentOrderInGroup() + 1L;
        this.commentGroupNumber = parentComment.getId();
        this.deep = parentComment.getDeep() + 1;
        this.isSecret = commentAddDto.getIsSecret();
        this.isDeleted = false;
        this.user = user;
        this.post = parentComment.getPost();
        this.parent = parentComment;
    }

    // groupNumber 값을 정할 수 없어서 commentId 값을 넣었음
    public void setCommentGroupNumber(Long commentGroupNumber) {
        this.commentGroupNumber = commentGroupNumber;
    }

    public void editComment(CommentEditDto commentEditDto) {
        this.content = commentEditDto.getContent();
        this.isSecret = commentEditDto.getIsSecret();
    }

    public void delete() {
        this.content = "content is deleted";
        this.isSecret = false;
        this.isDeleted = true;
//        this.user = null;
        this.notification = null;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isOwner(String userEmail) {
        if (this.user.getEmail().equals(userEmail)) {
            return true;
        }
        if (post.getUser().getEmail().equals(userEmail)) {
            return true;
        }
        return false;
    }

    @Override
    public Long getTargetObjectId() {
        return id;
    }

    @Override
    public Long getTargetUserId() {
        return user.getId();
    }

    @Override
    public String getFormatMessage(String actionUserName, String format) {
        return "내 댓글에 " + actionUserName + "님이 " + format;
    }

    @Override
    public String getFormatUrl() {
        String boardName = post.getBoard().getName();
        Long postId = post.getPostId();
        return "/board/" + boardName + "/post/" + postId;
    }

    @Override
    public String getFormat() {
        return "댓글을 남겼습니다.";
    }
}
