package com.kafka.exam.util;

import com.kafka.exam.conf.TimePrintType;
import com.kafka.exam.conf.annotation.TimeCheck;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

import java.util.function.Supplier;

@Slf4j
public class TimeCheckUtil {
    public static  <T> T check(Supplier<T> supplier) {
        long start = System.currentTimeMillis();
        try {
            return supplier.get();
        } finally {
            long end = System.currentTimeMillis();
            long totalTime = end - start;
            log.info("실행시간 [{}]ms", totalTime);
        }
    }
}

