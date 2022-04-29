package hello.rest.dto;

import hello.rest.entity.notification.Notification;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long notificationId;

    private LocalDateTime read_date;

    @NotEmpty
    private Boolean isRead;

    @NotEmpty
    private String message;

    @NotEmpty
    private String url;

    private String targetUserName;
    private String actionUserName;
    private String boardName;
    private String postTitle;
    private Long postId;
    private Long commentId;

    private String nowMinusCreatedAt;

    public NotificationDto(Notification notification) {
        this.notificationId = notification.getId();
        this.read_date = notification.getRead_date();
        this.isRead = notification.getIsRead();
        this.message = notification.getMessage();
        this.url = notification.getUrl();
        this.targetUserName = notification.getTargetUser().getName();
        this.actionUserName = notification.getActionUser().getName();
        this.boardName = notification.getComment().getPost().getBoard().getName();
        this.postTitle = notification.getComment().getPost().getTitle();
        this.postId = notification.getComment().getPost().getPostId();
        this.commentId = notification.getComment().getId();
        this.nowMinusCreatedAt = this.calculateDay(notification.getCreatedAt());
    }

    private String calculateDay(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long months = ChronoUnit.MONTHS.between(createdAt, now);
        long years = ChronoUnit.YEARS.between(createdAt, now);
        if (years > 0) {
            return years + "년 전";
        } else if (months > 0) {
            return months + "달 전";
        } else if (days > 0) {
            return days + "일 전";
        } else if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0){
            return minutes + "분 전";
        } else {
            return seconds + "초 전";
        }
    }
}
