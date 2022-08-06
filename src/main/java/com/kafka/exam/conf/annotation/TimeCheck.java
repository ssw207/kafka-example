package com.kafka.exam.conf.annotation;

import com.kafka.exam.conf.TimePrintType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeCheck {
    TimePrintType type() default TimePrintType.SEC;
}
