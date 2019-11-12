package com.horcrux.components.searchandfilter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation Definition
 * Created by midhun on 27/2/19.
 * @author midhun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface SearchAndFilter {
    String[] exclude() default {};

    String[] searchOver() default {};

    String name() default "";
}
