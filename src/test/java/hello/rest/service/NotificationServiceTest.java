package hello.rest.service;

import hello.rest.dto.NotificationCreateDto;
import hello.rest.dto.NotificationDto;
import hello.rest.dto.comment.CommentAddDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.notification.Notification;
import hello.rest.repository.NotificationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Profile("test")
@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationJpaRepository notificationJpaRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    private User userA, userB;
    private Board board;
    private List<Post> posts;
    private Comment comment;

    private User targetUserIsUserA;
    private User actionUserIsUserB;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void init() {
        userA = userService.findUser("a@a.com");
        userB = userService.findUser("b@b.com");
        board = boardService.findBoard("free");
        posts = postService.findPosts(board.getName());
        CommentAddDto commentAddDto = new CommentAddDto("testContent", false);
        comment = commentService.makeComment(userB, posts.get(0), commentAddDto);
        targetUserIsUserA = userA;
        actionUserIsUserB = userB;
    }

    @Test
    public void createNotificationTest() throws Exception {
        //given
        NotificationCreateDto notificationCreateDto = new NotificationCreateDto("testMessage", "testUrl");

        //when
        Notification notification = notificationService.createNotification(targetUserIsUserA.getId(), actionUserIsUserB, comment.getId(), notificationCreateDto);

        //then
        assertThat(notification.getRead_date()).isNull();
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getMessage()).isEqualTo(notificationCreateDto.getMessage());
        assertThat(notification.getUrl()).isEqualTo(notificationCreateDto.getUrl());
        assertThat(notification.getTargetUser()).isEqualTo(targetUserIsUserA);
        assertThat(notification.getActionUser()).isEqualTo(actionUserIsUserB);
        assertThat(notification.getComment()).isEqualTo(comment);

    }

    @Test
    @Rollback(false)
    public void createNotificationWithAnnotationTest() throws Exception {
        //given
        //when
        List<NotificationDto> availableNotifications = notificationService.findAvailableNotifications(targetUserIsUserA);

        //then
        assertThat(availableNotifications.size()).isEqualTo(1);
        assertThat(availableNotifications)
                .extracting("read_date")
                .containsNull();
        assertThat(availableNotifications)
                .extracting("isRead")
                .containsExactly(false);
        assertThat(availableNotifications)
                .extracting("message")
                .containsExactly("내 게시글에 " + actionUserIsUserB.getName() + "님이 댓글을 남겼습니다");
        assertThat(availableNotifications)
                .extracting("url")
                .containsExactly("/board/" + board.getName() + "/post/" + posts.get(0).getPostId());
        assertThat(availableNotifications)
                .extracting("targetUserName")
                .containsExactly(targetUserIsUserA.getName());
        assertThat(availableNotifications)
                .extracting("actionUserName")
                .containsExactly(actionUserIsUserB.getName());
    }

    @Test
    public void findAllAvailableNotifications() {
        //given
        for (int i = 0; i < 5; i++) {
            NotificationCreateDto notificationCreateDto =
                    new NotificationCreateDto("testMessage " + (i + 1), "testUrl " + (i + 1));
            notificationService.createNotification(targetUserIsUserA.getId(), actionUserIsUserB, comment.getId(), notificationCreateDto);
        }

        //when
        List<NotificationDto> availableNotifications = notificationService.findAvailableNotifications(targetUserIsUserA);

        //then
        assertThat(availableNotifications.size()).isEqualTo(5);
        assertThat(availableNotifications)
                .extracting("read_date")
                .containsExactly(null, null, null, null, null);
        assertThat(availableNotifications)
                .extracting("isRead")
                .containsExactly(false, false, false, false, false);
        assertThat(availableNotifications)
                .extracting("message")
                .containsExactly("testMessage 1", "testMessage 2", "testMessage 3", "testMessage 4", "testMessage 5");
        assertThat(availableNotifications)
                .extracting("url")
                .containsExactly("testUrl 1", "testUrl 2", "testUrl 3", "testUrl 4", "testUrl 5");
        assertThat(availableNotifications)
                .extracting("targetUserName")
                .containsExactly(targetUserIsUserA.getName(), targetUserIsUserA.getName(), targetUserIsUserA.getName(), targetUserIsUserA.getName(), targetUserIsUserA.getName());
        assertThat(availableNotifications)
                .extracting("actionUserName")
                .containsExactly(actionUserIsUserB.getName(), actionUserIsUserB.getName(), actionUserIsUserB.getName(), actionUserIsUserB.getName(), actionUserIsUserB.getName());

    }

    @Test
    public void deleteNotificationTest() throws Exception {
        //given
        NotificationCreateDto notificationCreateDto = new NotificationCreateDto("testMessage", "testUrl");
        Notification notification = notificationService.createNotification(targetUserIsUserA.getId(), actionUserIsUserB, comment.getId(), notificationCreateDto);

        //when
        em.flush();
        em.clear();
        commentService.deleteComment(comment.getId());

        //then
        List<Notification> all = notificationJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
        assertThat(notificationJpaRepository.findById(notification.getId())).isEmpty();
//        assertThrows(CResourceNotExistException.class, () -> {
//            notificationJpaRepository.findById(notification.getId());
//        });
    }

    @Test
    public void readNotificationTest() throws Exception {
        //given
        NotificationCreateDto notificationCreateDto = new NotificationCreateDto("testMessage", "testUrl");
        Notification notification = notificationService.createNotification(targetUserIsUserA.getId(), actionUserIsUserB, comment.getId(), notificationCreateDto);

        //when
        NotificationDto notificationDto = notificationService.readNotification(notification.getId());

        //then
        assertThat(notificationDto.getIsRead()).isTrue();
        assertThat(notificationDto.getRead_date()).isNotNull();
    }

    @Test
    public void findAllAvailableNotificationsWithReadNotification() throws Exception {
        //given
        List<Long> notificationIds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            NotificationCreateDto notificationCreateDto =
                    new NotificationCreateDto("testMessage " + (i + 1), "testUrl " + (i + 1));
            Notification notification = notificationService.createNotification(targetUserIsUserA.getId(), actionUserIsUserB, comment.getId(), notificationCreateDto);
            notificationIds.add(notification.getId());
        }
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Long notificationId : notificationIds) {
            notificationDtos.add(notificationService.readNotification(notificationId));
        }

        //when
        List<NotificationDto> availableNotifications = notificationService.findAvailableNotifications(targetUserIsUserA);

        //then
        assertThat(availableNotifications.size()).isEqualTo(0);
    }
}