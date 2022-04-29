package hello.rest.config.security;

import hello.rest.advice.exception.CAuthenticationEntryPointException;
import hello.rest.advice.exception.CNotOwnerException;
import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import hello.rest.service.CommentService;
import hello.rest.service.PostService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

//https://zuminternet.github.io/ZUM-Pilot-dynamic-board/
//https://hodolman.com/19
// 1. PermissionEvaluator Interface 구현체 CustomPermissionEvaluator 정의
@AllArgsConstructor
@NoArgsConstructor
@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    //https://kim-jong-hyun.tistory.com/26
//    private MessageSourceAccessor messageSourceAccessor;
    private PostService postService;
    private CommentService commentService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        log.info("hasPermission(Authentication authentication, Object targetDomainObject, Object permission)");
        throw new UnsupportedOperationException("This method is not supported by this application");
//        return false;
    }

    /**
     * 해당 객체의 수정, 삭제 권한이 있는 확인하는 메서드
     *
     * @param authentication 현재 인증된 사용자의 정보
     * @param targetId 접근 권한을 확인할 타겟 객체의 pk
     * @param targetType 타겟 객체의 클래스 타입
     * @param permission 인자로 받은 String data
     * @return 해당 객체의 수정, 삭제 권한이 있으면 return true, 없으면 throw UnAuthorizedException
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetId == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        Long convertId = (Long) targetId;
        String targetTypeUpper = targetType.toUpperCase();
        log.debug("target 객체의 pk={}, target 클래스 type={}", convertId, targetTypeUpper);
        log.info("target 객체의 pk={}, target 클래스 type={}, permission", convertId, targetTypeUpper, permission);

        boolean result = checkIsOwner(authentication, getTargetObject(convertId, targetTypeUpper), permission.toString().toUpperCase());
        if (!result) {
            throw new CAuthenticationEntryPointException();
        }
        return true;
    }

    /**
     * targetType에 따라 Service Layer에서 해당 겍체를 가져와 Object로 변환하는 메서드
     */
    private Object getTargetObject(long targetId, String targetType) {
        if ("POST".equals(targetType)) {
            return postService.getPost(targetId);
        } else if ("COMMENT".equals(targetType)) {
            return commentService.findComment(targetId);
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자에 target 객체를 수정/삭제할 권한이 있는지 확인하는 메서드
     * 권한이 있으면 return true, 없으면 return false
     */
    private boolean checkIsOwner(Authentication authentication, Object targetDomainObject, String permission) {
        if (targetDomainObject == null) {
            throw new CNotOwnerException("현재 로그인한 사용하자는 수정/삭제 권한이 없습니다");
        }

        String userEmail = authentication.getName();

        if (targetDomainObject instanceof Post) {
            return isOwnerOfPost((Post) targetDomainObject, userEmail);
        } else if (targetDomainObject instanceof Comment) {
            return isOwnerOfComment((Comment) targetDomainObject, userEmail);
        }
        return false;
    }

    private boolean isOwnerOfPost(Post post, String userEmail) {
        return post.isOwner(userEmail);
    }

    private boolean isOwnerOfComment(Comment comment, String userEmail) {
        return comment.isOwner(userEmail);
    }
}
