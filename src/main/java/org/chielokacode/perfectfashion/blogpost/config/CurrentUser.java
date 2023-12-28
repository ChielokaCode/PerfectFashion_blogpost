package org.chielokacode.perfectfashion.blogpost.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/*
This allows Spring to inject the authentication details of the currently
authenticated user into this parameter, providing easy access to
user information within the method.
 */
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
