package com.kafka.exam.util;

import com.kafka.exam.conf.TimePrintType;
import com.kafka.exam.conf.aop.code.TestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TimeCheckUtilTest {

    @Test
    void name() {
        TestService testService = new TestService();
        String str ="테스트";
        String result = TimeCheckUtil.check(() -> testService.testUtil(str));
        assertThat(result).isEqualTo(str);
    }
}