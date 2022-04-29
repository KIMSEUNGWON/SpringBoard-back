package hello.rest.service.notification;

import hello.rest.entity.notification.Notification;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class NotificationAlert {

    private Long uid;
    private int notificationCount;
    private List<Notification> notifications;
}
