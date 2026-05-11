package com.xyz.enterprise.common.annotation;

import com.xyz.enterprise.common.validation.ModelValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
  boolean value() default true;

  String messageOnAbsence() default ModelValidator.DEFAULT_MESSAGE;

  Class[] operations() default {};
}
