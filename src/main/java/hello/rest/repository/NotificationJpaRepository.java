package hello.rest.repository;

import hello.rest.dto.NotificationDto;
import hello.rest.entity.User;
import hello.rest.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    @Query("select new hello.rest.dto.NotificationDto(n) " +
            "from Notification n where n.targetUser = :targetUser and n.isRead = false")
    List<NotificationDto> findAllAvailableNotificationsByUser(@Param("targetUser") User targetUser);

    @Query("select new hello.rest.dto.NotificationDto(n) " +
            "from Notification n where n.targetUser = :targetUser")
    List<NotificationDto> findAllNotificationsByUser(@Param("targetUser") User targetUser);

    @Query("select n from Notification n join n.targetUser where n.targetUser.id = :userId and n.isRead = false")
    List<Notification> findByNotificationTargetUserIdAndIsReadIsFalse(@Param("userId") Long userId);

    @Query("select new hello.rest.dto.NotificationDto(n) " +
            "from Notification n where n.targetUser.id = :userId and n.isRead = false")
    List<NotificationDto> findAllUnReadNotification(@Param("userId") Long userId);

    @Modifying
    @Query("update Notification n set n.isRead = true where n.id in :notificationIds")
    int updateNotificationIsRead(@Param("notificationIds") List notificationIds);

    @Query("select count(n) from Notification n where n.id in :notificationIds")
    int isNotificationIdsIn(@Param("notificationIds") List notificationIds);
}
