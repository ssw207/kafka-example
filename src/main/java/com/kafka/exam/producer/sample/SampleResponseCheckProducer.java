package com.kafka.exam.producer.sample;

import com.kafka.exam.partitoner.sample.SampleCustomPartitioner;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class SampleResponseCheckProducer {
    private final static String TOPIC_NAME = "test"; // 토픽이름
    private final static String BOOTSTRAP_SERVERS = "localhost:9095"; // 브로커 주소

    public static void main(String[] args) {
        SampleResponseCheckProducer producer = new SampleResponseCheckProducer();
        producer.send();
    }

    public void send() {
        /**
         * 메시지 키가 goToPartition1인 데이터는 커스텀 파티셔너 SampleCustomPartitioner에 의해 파티션1로 지정됨
         */
        KafkaProducer<String, String> producer = getProducer();
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME,  "meta data check");

        Future<RecordMetadata> send = producer.send(record);
        try {
            // 프로듀서 전송결과를 동기로 받아온다.
            RecordMetadata recordMetadata = send.get();
            // 토픽, 오프셋, 파티션 정보를 알수있다
            // ex) aks 옵션 1인경우 (리더파티션에 저장되는것 까지 확인) : com.kafka.exam.producer.sample.SampleResponseCheckProducer - 프로듀서 전송 정보 : test-0@7 -> test 토픽에 0번 파티션 에 7번 오프셋에 저장됨
            // ex) aks 옵션 0인경우 (프로듀선에 전송만) : com.kafka.exam.producer.sample.SampleResponseCheckProducer - 프로듀서 전송 정보 : test-0@-1 -> test 토픽에 0번 파티션 어떤 오프셋에 저장되는지는 모름
            log.info("프로듀서 전송 정보 : {}",recordMetadata.toString());
        } catch (Exception e) {
            log.error("error ", e);
        } finally {
            //send 실행시 바로 전송되는것이 아니라 파티셔너를 통해 어커뮤레이터에 적제되므로 flush를 호출해야 전송된다
            producer.flush();
            producer.close();
        }
    }

    private KafkaProducer<String, String> getProducer() {
        // 프로듀서 설정
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); // 카프카 브로커 서버 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key 직렬화 클래스
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value 직렬화 클래스
        config.put(ProducerConfig.ACKS_CONFIG, "0");
        return new KafkaProducer<>(config); // KafkaProducer<key타입, value타입>
    }
}
