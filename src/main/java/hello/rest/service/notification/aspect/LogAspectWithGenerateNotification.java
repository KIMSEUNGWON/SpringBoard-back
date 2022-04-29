package hello.rest.service.notification.aspect;

import hello.rest.dto.NotificationCreateDto;
import hello.rest.entity.Noticeable;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Optional;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogAspectWithGenerateNotification {

    // 알림으로 웹소켓 쓰기에는 무리, 페이지 이동할 때마다 갱신하거나 특정 시간 경과 후 갱신
    private final NotificationService notificationService;

    @Around("@annotation(logExecutionTime)")
    public Object doLogExecutionTime(ProceedingJoinPoint joinPoint,
                                   LogExecutionTime logExecutionTime) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // @LogExecutionTime 애노테이션이 붙어있는 타겟 메소드를 실행
        Object proceed = joinPoint.proceed();

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        Object[] args = joinPoint.getArgs();
        // post, comment를 상속 구조로 변경
//        for (Object arg : args) {
//            if (arg instanceof Noticeable) {
//                noticeable = (Noticeable) arg;
//                targetUserId = noticeable.getTargetUserId();
//            }
//        }
//        Comment targetComment = (Comment) proceed;
//        User actionUser = targetComment.getUser();
        User actionUser = null;
        Long targetUserId = null;
        Noticeable targetObject = (Noticeable) proceed;
        Noticeable noticeable = null;

        Optional<Object> any = Arrays.stream(args)
                .filter(arg -> arg instanceof Noticeable)
                .findAny();

        if (any.isPresent()) {
            Object o = any.get();
            noticeable = (Noticeable) o;
            targetUserId = noticeable.getTargetUserId();
        }

        Optional<Object> anyUser = Arrays.stream(args)
                .filter(arg -> arg instanceof User)
                .findAny();

        if (anyUser.isPresent()) {
            Object o = anyUser.get();
            actionUser = (User) o;
        }

//        if (targetUserId.equals(actionUser.getId())) {
//            log.info("same user");
//        } else {
//            log.info("diff user");
//            NotificationCreateDto notificationCreateDto =
//                    new NotificationCreateDto(noticeable.getFormatMessage(actionUser.getName()), noticeable.getFormatUrl());
//
//            log.info("notificationCreateDto={}", notificationCreateDto.getMessage());
//            log.info("notificationCreateDto={}", notificationCreateDto.getUrl());
//            log.info("targetUserId={}", targetUserId);
//            log.info("noticeable={}", noticeable.getTargetObjectId());
//
//            notificationService.createNotification(targetUserId, actionUser, targetComment.getId(), notificationCreateDto);
//        }

        if (isSomeoneLeaveComment(targetUserId, actionUser.getId())) {
            NotificationCreateDto notificationCreateDto =
                    new NotificationCreateDto(noticeable.getFormatMessage(actionUser.getName(), targetObject.getFormat()), noticeable.getFormatUrl());

            if (targetObject instanceof Comment) {
                notificationService.createNotification(targetUserId, actionUser, targetObject.getTargetObjectId(), notificationCreateDto);
            } else {
                notificationService.createNotification(targetUserId, actionUser, logExecutionTime.type(), notificationCreateDto);
            }
        }

        log.info("type={}", logExecutionTime.type());
        log.info("[aspect] {} args={}", joinPoint.getSignature(), args);

        return proceed;
    }

    private boolean isSomeoneLeaveComment(Long targetUserId, Long actionUserId) {
        if (targetUserId == null || actionUserId == null) {
            return false;
        }

        if (!targetUserId.equals(actionUserId)) {
            return true;
        }

        return false;
    }
}
