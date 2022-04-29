package hello.rest.service.notification;

import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.config.security.JwtTokenProvider;
import hello.rest.entity.User;
import hello.rest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/api/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam(required = false) String uuid,
                                                HttpServletRequest request) {


        final SseEmitter emitter = new SseEmitter();

        final User user = this.getUserFromCookies(request.getCookies());
        if (user == null || uuid == null) {
            throw new CResourceNotExistException("user or uuid가 존재하지 않습니다");
        }
        final StreamDataSet DATA_SET = new StreamDataSet(user, emitter);
        final String UNIQUE_UUID = uuid;

        try {
//            List<Integer> arr = new ArrayList<>();
//            if (!arr.isEmpty()) {
//                emitter.send(arr);
//            }
            notificationService.addEmitter(UNIQUE_UUID, DATA_SET);
            //front에서 지속적으로 eventStream통신을 한다
            Thread.sleep(2000);
        } catch (Exception e) {
//            e.printStackTrace();
//            emitter.completeWithError(e);
//            return null;
            throw new IllegalStateException("예상치 못한 예외 발생");
        }

        emitter.onCompletion(() -> {
            log.info("onCompletion");
            notificationService.removeEmitter(UNIQUE_UUID);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout");
            notificationService.removeEmitter(UNIQUE_UUID);
        });

//        emitter.complete();

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    // spring security jwt_token 값으로 유저 찾기 https://jason-moon.tistory.com/132
    private User getUserFromCookies(Cookie[] cookies) {

        for (Cookie cookie : cookies) {
            log.info("cookie.getName()={}", cookie.getName());
            log.info("cookie.getName()={}", cookie.getValue());
            if (cookie.getName().equals("til_auth")) {
                Authentication authentication = jwtTokenProvider.getAuthentication(cookie.getValue());
                log.info("authentication={}", authentication.getName());
                User user = (User) authentication.getPrincipal();
                log.info("user.getName()={}", user.getName());
                return user;
            }
        }
        return null;
    }
}
