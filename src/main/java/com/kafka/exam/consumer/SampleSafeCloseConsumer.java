package com.kafka.exam.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by seungwoo.song on 2022-08-29
 */
@Slf4j
public class SampleSafeCloseConsumer {
	private final static String TOPIC_NAME = "test"; // 토픽이름
	private final static String GROUP_ID = "test-group"; // 토픽이름
	private final static String BOOTSTRAP_SERVERS = "localhost:9095"; // 브로커 주소
	private final static KafkaConsumer<String, String> consumer = getConsumer();

	public static void main(String[] args) {
		// 현재 실행중인 쓰레드에 훅 추가
		// kill -term 으로 쓰레드 종료시 ShutdownThread의 run()이 실행되며 컨슈머의 wakeup() 메소드 실행
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());

		try {

			while (true) {
				// 컨슈머의 wakeup() 메소드 실행이후 poll() 실행시 WakeupException 발생
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1)); // 브로커로부터 데이터를 배치로 가져온다
				for (ConsumerRecord<String, String> record : records) {
					log.info("record:{}", record);
				}
				consumer.commitAsync();
			}

		} catch (WakeupException e) {
			log.warn("Wakup consumer");
		} finally {
			// WakeupException을 받은뒤 close 호출
			log.warn("컨슈머 종료");
			consumer.close();
		}

	}

	private static KafkaConsumer<String, String> getConsumer() {
		Properties configs = new Properties();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);

		consumer.subscribe(Arrays.asList(TOPIC_NAME));
		return consumer;
	}

	private static class ShutdownThread extends Thread {
		@Override 
		public void run() {
			log.info("Shutdown hook 호출. 쓰레드 종료");
			consumer.wakeup(); // wakeup 메서드 호출이후 poll이 호출되면 Wakeup Exception이 발생한다
		}
	}
}
