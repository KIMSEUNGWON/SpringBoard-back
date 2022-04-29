package hello.rest.config.security;

import hello.rest.service.CommentService;
import hello.rest.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

// 2. Java Config에서의 MethodSecurity 설정
@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = false)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

//    private final MessageSourceAccessor messageSourceAccessor;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(messageSourceAccessor, postService, commentService);
        CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, commentService);

        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);

        return expressionHandler;
    }
}
