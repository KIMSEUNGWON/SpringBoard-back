package hello.rest.controller.notification;

import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.dto.NotificationCreateDto;
import hello.rest.dto.NotificationDto;
import hello.rest.dto.NotificationIdsDto;
import hello.rest.entity.User;
import hello.rest.entity.notification.Notification;
import hello.rest.model.response.CommonResult;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.NotificationService;
import hello.rest.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/notification")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final ResponseService responseService;

    @GetMapping("/")
    public ListResult<NotificationDto> findAllAvailableNotifications(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new CResourceNotExistException();
        }

        List<NotificationDto> availableNotifications =
                    notificationService.findAllNotifications(user);
        return responseService.getListResult(availableNotifications);
    }

    @GetMapping("/unread")
    public ListResult<NotificationDto> findAllUnReadNotifications(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new CResourceNotExistException();
        }

        List<NotificationDto> availableNotifications =
                notificationService.findAllUnReadNotifications(user);
        return responseService.getListResult(availableNotifications);
    }

    @PostMapping("/")
    public SingleResult<NotificationDto> createNotification(@RequestParam Long targetUserId,
                                                            @RequestParam Long targetCommentId,
                                                            @RequestBody NotificationCreateDto notificationCreateDto,
                                                            @AuthenticationPrincipal User user) {

        if (targetUserId == null || user == null) {
            throw new CResourceNotExistException();
        }

        Notification notification = notificationService.createNotification(targetUserId, user, targetCommentId, notificationCreateDto);
        NotificationDto result = new NotificationDto(notification);
        return responseService.getSingleResult(result);
    }

    @PostMapping("/{notificationId}/")
    public SingleResult<NotificationDto> readNotification(@PathVariable Long notificationId,
                                                          @AuthenticationPrincipal User user) {

        if (notificationId == null || user == null) {
            throw new CResourceNotExistException();
        }

        NotificationDto notificationDto = notificationService.readNotification(notificationId);
        return responseService.getSingleResult(notificationDto);
    }

    //https://goateedev.tistory.com/284
    // TODO: 2022-04-20 읽지 않은 모든 알림 500에러 해결하기 -> controller는 list를 requestBody로 받지 못한다
    @PostMapping("/readAll")
    public SingleResult<Integer> readAllNotification(@RequestBody NotificationIdsDto notificationIdsDto,
                                                     @AuthenticationPrincipal User user) {

        if (notificationIdsDto == null || user == null) {
            throw new CResourceNotExistException();
        }

        log.info("notificationIdsDto={}", notificationIdsDto);
        log.info("notificationIdsDto.getNotificationIds()={}", notificationIdsDto.getNotificationIds());

        int result = 0;

        if (notificationService.isValidIds(notificationIdsDto.getNotificationIds())) {
            result = notificationService.readAllNotification(notificationIdsDto.getNotificationIds());
        }

        return responseService.getSingleResult(result);
    }

    @DeleteMapping("/{notificationId}/")
    public CommonResult deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new CResourceNotExistException();
        }

        notificationService.deleteNotification(user, notificationId);

        return responseService.getSuccessResult();
    }
}
