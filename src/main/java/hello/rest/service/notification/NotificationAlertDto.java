package hello.rest.service.notification;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotificationAlertDto {

    private String message;
    private String url;
    private Long targetUserId;
    private Long actionUserId;
    private Long commentId;
}
