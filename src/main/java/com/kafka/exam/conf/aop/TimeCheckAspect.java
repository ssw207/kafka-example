package com.kafka.exam.conf.aop;

import com.kafka.exam.conf.TimePrintType;
import com.kafka.exam.conf.annotation.TimeCheck;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
public class TimeCheckAspect {

    @Pointcut("execution(* com.kafka.exam..*(..))") // com.kafka.exam 패키지 이하 모두 적용
    public void allPackage() {}

    @Pointcut("@annotation(com.kafka.exam.conf.annotation.TimeCheck)") // 어노테이션이 있는
    public void timeCheckAnnotation() {}

    @Around("allPackage() && @annotation(timeCheckAnnotation)")
    public Object timeCheck(ProceedingJoinPoint joinPoint, TimeCheck timeCheckAnnotation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("{} {} {}",stopWatch.getTotalTimeMillis(), stopWatch.getTotalTimeSeconds(), stopWatch.getTotalTimeNanos());
            log.info("MethodName:[{}] / 실행시간:[{}][{}]", joinPoint.getSignature().getName(), getTime(stopWatch, timeCheckAnnotation.type()), timeCheckAnnotation.type());
        }
    }

    private Object getTime(StopWatch stopWatch, TimePrintType type) {
        switch (type) {
            case SEC:
                return stopWatch.getTotalTimeSeconds();

            case MS:
                return stopWatch.getTotalTimeMillis();

        }

        throw new IllegalArgumentException("TimePrintType is not valid");
    }
}



