package com.kafka.exam.exam.producer;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class SampleProducer {

    private static final String BOOTSTRAP_SERVER = "";


    public void run() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // key, value 타입
        KafkaProducer<String, String> producer = new KafkaProducer<>(config);

        String testMessage = "testMessage";
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("key", testMessage);
    }
}
