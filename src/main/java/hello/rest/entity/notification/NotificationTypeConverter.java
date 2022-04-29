package hello.rest.entity.notification;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;
import java.util.NoSuchElementException;

public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType attribute) {
        return attribute.getCode();
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        return EnumSet.allOf(NotificationType.class).stream()
                .filter(notificationType -> notificationType.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException());
    }
}
