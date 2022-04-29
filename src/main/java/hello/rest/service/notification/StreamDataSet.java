package hello.rest.service.notification;

import hello.rest.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StreamDataSet {

    private User user;
    private SseEmitter sseEmitter;
}
