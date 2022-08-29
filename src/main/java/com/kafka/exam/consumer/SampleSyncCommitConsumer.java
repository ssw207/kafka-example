package com.kafka.exam.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by seungwoo.song on 2022-08-29
 */
@Slf4j
public class SampleSyncCommitConsumer {
	private final static String TOPIC_NAME = "test"; // 토픽이름
	private final static String GROUP_ID = "test-group"; // 토픽이름
	private final static String BOOTSTRAP_SERVERS = "localhost:9095"; // 브로커 주소

	public static void main(String[] args) {
		Properties configs = new Properties();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동커밋


		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);

		consumer.subscribe(Arrays.asList(TOPIC_NAME));

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1)); // 브로커로부터 데이터를 배치로 가져온다
			for (ConsumerRecord<String, String> record : records) {
				log.info("record:{}", record);
			}
			consumer.commitSync(); // 레코드를 모두 읽은뒤 동기적으로 커밋한다
		}
	}
}
