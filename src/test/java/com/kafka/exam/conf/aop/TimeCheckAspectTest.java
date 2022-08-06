package com.kafka.exam.conf.aop;

import com.kafka.exam.conf.aop.code.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(TimeCheckAspect.class)
@SpringBootTest
class TimeCheckAspectTest {

    @Autowired
    TestService testService;

    @Test
    void timeAspectApplyTest() {
        testService.test();
        assertThat(AopUtils.isAopProxy(testService)).isTrue();
    }

    @Test
    void timeAspectTest() {
        testService.test();
        testService.testSec();
        testService.test();
    }
}