package hello.rest.service;

import hello.rest.advice.exception.CNotOwnerException;
import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.dto.NotificationCreateDto;
import hello.rest.dto.NotificationDto;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.notification.Notification;
import hello.rest.entity.notification.NotificationType;
import hello.rest.repository.NotificationJpaRepository;
import hello.rest.repository.UserJpaRepository;
import hello.rest.repository.comment.CommentJpaRepository;
import hello.rest.service.notification.NotificationAlert;
import hello.rest.service.notification.NotificationAlertDto;
import hello.rest.service.notification.StreamDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class NotificationService {

    private final NotificationJpaRepository notificationJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    public Notification createNotification(Long targetUserId,
                                           User actionUser,
                                           Long targetCommentId,
                                           NotificationCreateDto notificationCreateDto) {

        User targetUser = userJpaRepository.findById(targetUserId)
                .orElseThrow(CResourceNotExistException::new);

        Comment targetComment = commentJpaRepository.findById(targetCommentId)
                .orElseThrow(CResourceNotExistException::new);

        Notification notification = new Notification(
                targetUser,
                actionUser,
                targetComment,
                notificationCreateDto);

        Notification saveNotification = notificationJpaRepository.save(notification);
        return saveNotification;
    }

    public Notification createNotification(Long targetUserId,
                                           User actionUser,
                                           String notificationType,
                                           NotificationCreateDto notificationCreateDto) {

        User targetUser = userJpaRepository.findById(targetUserId)
                .orElseThrow(CResourceNotExistException::new);

        Notification notification = Notification.makeNotification(
                targetUser,
                actionUser,
                NotificationType.valueOf(notificationType),
                notificationCreateDto);

        Notification saveNotification = notificationJpaRepository.save(notification);
        return saveNotification;
    }

    public List<NotificationDto> findAvailableNotifications(User targetUser) {

        List<NotificationDto> result = notificationJpaRepository.findAllAvailableNotificationsByUser(targetUser);

        return result;
    }

    public List<NotificationDto> findAllNotifications(User targetUser) {

        List<NotificationDto> result = notificationJpaRepository.findAllNotificationsByUser(targetUser);

        return result;
    }

    public List<NotificationDto> findAllUnReadNotifications(User user) {

//        List<Notification> notifications = notificationJpaRepository.findByNotificationTargetUserIdAndIsReadIsFalse(user.getId());
//
//        List<NotificationDto> result = notifications.stream()
//                .map((notification) -> {
//                    NotificationDto notificationDto = new NotificationDto(notification);
//                    return notificationDto;
//                })
//                .collect(Collectors.toList());

        List<NotificationDto> result = notificationJpaRepository.findAllUnReadNotification(user.getId());

        return result;
    }

    public NotificationDto readNotification(Long notificationId) {
        Notification notification = notificationJpaRepository.findById(notificationId)
                .orElseThrow(CResourceNotExistException::new);

        notification.read();

        NotificationDto result = new NotificationDto(notification);
        return result;
    }

    public int readAllNotification(List<Long> notificationIds) {
        int result = notificationJpaRepository.updateNotificationIsRead(notificationIds);

        return result;
    }

    public void deleteNotification(User user, Long notificationId) {

        if (!isOwner(user, notificationId)) {
            throw new CNotOwnerException("알림 소유자가 아닙니다.");
        }

        notificationJpaRepository.deleteById(notificationId);
    }

    public boolean isValidIds(List<Long> notificationIds) {
        int countNotificationIdsIn = notificationJpaRepository.isNotificationIdsIn(notificationIds);
        if (notificationIds.size() != countNotificationIdsIn) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isOwner(User user, Long notificationId) {
        Notification findNotification = notificationJpaRepository.findById(notificationId)
                .orElseThrow(CResourceNotExistException::new);

        // getTargetUser는 proxy라서 비즈니스 키로 값 비교
        if (findNotification.getTargetUser().getId() == user.getId()) {
            return true;
        }

        return false;
    }

    private final ConcurrentHashMap<String, StreamDataSet> eventMap = new ConcurrentHashMap<>();

    public void addEmitter(final String UNIQUE_UUID, final StreamDataSet dataSet) {
        eventMap.put(UNIQUE_UUID, dataSet);
    }

    public void removeEmitter(final String UNIQUE_UUID) {
        eventMap.remove(UNIQUE_UUID);
    }

    // @Scheduled 사용법 https://rooted.tistory.com/12
//    @Scheduled(initialDelay = 2000L, fixedRate = 115000L)
    public void fetch() {
        log.info("eventMap.size={}", eventMap.size());
        if (eventMap.size() == 0) {
            return;
        }
        this.handleAlert();
    }

    public void handleAlert() {
        List<String> toBeRemoved = new ArrayList<>(eventMap.size());
        List<Long> alertIdList = new ArrayList<>();

        for (Map.Entry<String, StreamDataSet> entry : eventMap.entrySet()) {
            final String uniqueKey = entry.getKey();
            final StreamDataSet dataSet = entry.getValue();

            final User user = dataSet.getUser();
            final List<Notification> receivingAlert = notificationJpaRepository.findByNotificationTargetUserIdAndIsReadIsFalse(user.getId());
            final int noneReadCount = receivingAlert.size();

            if (noneReadCount == 0) {
                continue;
            }

            log.info("receivingAlert={}", receivingAlert);

            final SseEmitter emitter = dataSet.getSseEmitter();

            final List<Notification> alertList = getListAnMinuteAndAlertFalse(receivingAlert);

            if (alertList.size() == 0) {
                continue;
            }

            log.info("alertList={}", alertList.get(0));

            List<NotificationAlertDto> notificationAlertDtos = alertList.stream()
                    .map((notification) -> {
                        NotificationAlertDto notificationAlertDto = NotificationAlertDto.builder()
                                .message(notification.getMessage())
                                .url(notification.getUrl())
                                .targetUserId(notification.getTargetUser().getId())
                                .actionUserId(notification.getActionUser().getId())
                                .commentId(notification.getComment().getId())
                                .build();
                        return notificationAlertDto;
                    })
                    .collect(Collectors.toList());

            NotificationAlert alert = NotificationAlert.builder()
                    .uid(user.getId())
                    .notificationCount(noneReadCount)
                    .notifications(alertList)
                    .build();

            log.info("alert={}", alert);

            alertIdList.addAll(alertList.stream()
                    .map(Notification::getId)
                    .collect(Collectors.toList()));
            log.info("alertIdList={}", alertIdList);

            try {
                log.info("sending emitter");
//                emitter.send(alert, MediaType.APPLICATION_JSON);
                emitter.send(notificationAlertDtos, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                log.error("emitter sending error::{}", e.getMessage());
                toBeRemoved.add(uniqueKey);
            }

            updateIsAlert(alertIdList);

            for (String uuid : toBeRemoved) {
                eventMap.remove(uuid);
            }
        }
    }

    // LocalDateTime 비교 https://kyhyuk.tistory.com/187
    private List<Notification> getListAnMinuteAndAlertFalse(List<Notification> receivingAlert) {

        List<Notification> validNotificationList = new ArrayList<>();

        for (Notification notification : receivingAlert) {
            LocalDateTime notificationCreatedAt = notification.getCreatedAt();
            LocalDateTime nowMinus30Minutes = LocalDateTime.now().minusMinutes(30);
            if (!notificationCreatedAt.isBefore(nowMinus30Minutes) && notification.getIsAlert() == false) {
                validNotificationList.add(notification);
            }
        }

        return validNotificationList;
    }

    private void updateIsAlert(List<Long> alertIdList) {
        for (Long aLong : alertIdList) {
            Notification notification = notificationJpaRepository.findById(aLong).orElseThrow(() -> new NoSuchElementException());
            notification.alert();
        }
    }
}
