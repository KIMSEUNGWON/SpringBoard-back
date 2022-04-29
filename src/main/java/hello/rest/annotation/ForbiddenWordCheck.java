package hello.rest.annotation;

import hello.rest.dto.BoardDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForbiddenWordCheck {
    String param() default "boardDto.content";

    Class<?> checkClazz() default BoardDto.class;
}
