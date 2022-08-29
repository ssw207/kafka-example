package com.kafka.exam.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by seungwoo.song on 2022-08-29
 */
@Slf4j
public class SampleAsyncCommitConsumer {
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

			// 비동기 커밋후 콜백으로 응답값을 처리한다
			consumer.commitAsync((offsets, exception) -> {
				if (exception != null) {
					log.error("commit failed for offset:{}", offsets, exception);
				} else {
					log.error("commit success");
				}
			});
		}
	}
}
