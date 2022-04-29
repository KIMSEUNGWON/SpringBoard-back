package hello.rest.entity.notification;

import hello.rest.dto.NotificationCreateDto;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.common.CommonDateEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"isRead", "message", "url", "isAlert", "notificationType"})
public class Notification extends CommonDateEntity {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private LocalDateTime read_date;

    @Column
    private Boolean isRead;

    @Column
    private String message;

    @Column
    private String url;

    @Column
    private Boolean isAlert;

    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType notificationType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "action_user_id")
    private User actionUser;

    @OneToOne(mappedBy = "notification")
    private Comment comment;

    // 댓글 알림 전용
    public Notification(User targetUser, User actionUser, Comment targetComment, NotificationCreateDto notificationCreateDto) {
        this.read_date = null;
        this.isRead = false;
        this.message = notificationCreateDto.getMessage();
        this.url = notificationCreateDto.getUrl();
        this.isAlert = false;
        this.notificationType = NotificationType.Comment;
        this.targetUser = targetUser;
        this.actionUser = actionUser;
        setComment(targetComment);
    }

    public void read() {
        this.read_date = LocalDateTime.now();
        this.isRead = true;
    }

    public void alert() {
        this.isAlert = true;
    }

    private void setComment(Comment targetComment) {
        this.comment = targetComment;
        targetComment.setNotification(this);
    }

    // 범용 알람 전용
    private Notification(User targetUser, User actionUser, NotificationType notificationType, NotificationCreateDto notificationCreateDto) {
        this.read_date = null;
        this.isRead = false;
        this.message = notificationCreateDto.getMessage();
        this.url = notificationCreateDto.getUrl();
        this.isAlert = false;
        this.notificationType = notificationType;
        this.targetUser = targetUser;
        this.actionUser = actionUser;
    }

    public static Notification makeNotification(User targetUser, User actionUser, NotificationType notificationType, NotificationCreateDto notificationCreateDto) {
        return new Notification(targetUser, actionUser, notificationType, notificationCreateDto);
    }
}
