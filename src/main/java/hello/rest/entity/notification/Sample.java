package hello.rest.entity.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sample {

    @Id
    private String id;
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType notificationType;
}
