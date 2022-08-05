package com.kafka.exam.exam.producer;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class SampleProducer {

    private static final String BOOTSTRAP_SERVER = "";


    public void run() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);

    }
}
