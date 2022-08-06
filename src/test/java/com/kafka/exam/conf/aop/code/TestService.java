package com.kafka.exam.conf.aop.code;

import com.kafka.exam.conf.TimePrintType;
import com.kafka.exam.conf.annotation.TimeCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {
    @TimeCheck
    public String test() {
        log.info(TestService.class.getName());
        return "ok";
    }

    @TimeCheck(type = TimePrintType.SEC)
    public String testSec() {
        log.info(TestService.class.getName());
        return "ok";
    }
}
