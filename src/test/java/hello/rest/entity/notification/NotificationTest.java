package hello.rest.entity.notification;

import hello.rest.dto.NotificationCreateDto;
import hello.rest.dto.comment.CommentAddDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.repository.NotificationJpaRepository;
import hello.rest.service.CommentService;
import hello.rest.service.PostService;
import hello.rest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class NotificationTest {

    @Autowired
    NotificationJpaRepository notificationJpaRepository;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    // 공지글을 작성하는 경우
    @Test
    public void createNoticeNotificationTest() throws Exception {
        //given
        User actionUser = userService.findUser("a@a.com");
        User targetUser = userService.findUser("b@b.com");

        NotificationCreateDto notificationCreateDto = new NotificationCreateDto("message", "url");

        //when
        Notification notification = Notification.makeNotification(targetUser, actionUser, NotificationType.Notice, notificationCreateDto);

        //then
        assertThat(notification.getRead_date()).isNull();
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getMessage()).isEqualTo("message");
        assertThat(notification.getUrl()).isEqualTo("notice");
        assertThat(notification.getIsAlert()).isFalse();
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.Notice);
        assertThat(notification.getTargetUser()).isEqualTo(targetUser);
        assertThat(notification.getActionUser()).isEqualTo(actionUser);
    }

    // b가 a의 게시글에 댓글을 작성하는 경우
    @Test
    public void createCommentNotificationTest() throws Exception {
        //given
        User actionUser = userService.findUser("a@a.com");
        User targetUser = userService.findUser("b@b.com");
        List<Post> posts = postService.findPosts("free");
        CommentAddDto commentAddDto = new CommentAddDto("content", false);
        Comment comment = commentService.makeComment(targetUser, posts.get(0), commentAddDto);
        NotificationCreateDto notificationCreateDto = new NotificationCreateDto("message", "url");

        //when
        Notification notification = new Notification(targetUser, actionUser, comment, notificationCreateDto);

        //then
        assertThat(notification.getRead_date()).isNull();
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getMessage()).isEqualTo("message");
        assertThat(notification.getUrl()).isEqualTo("url");
        assertThat(notification.getIsAlert()).isFalse();
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.Comment);
        assertThat(notification.getTargetUser()).isEqualTo(targetUser);
        assertThat(notification.getActionUser()).isEqualTo(actionUser);
    }

}