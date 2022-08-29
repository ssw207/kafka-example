package com.kafka.exam.producer;



import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * 카프카 브로커로 메시지 값을 단순 1회 전송하는 샘플코드
 */
@Slf4j
public class SampleProducer {
    private final static String TOPIC_NAME = "test"; // 토픽이름
    private final static String BOOTSTRAP_SERVERS = "localhost:9095"; // 브로커 주소
    private final KafkaProducer<String, String> producer = getProducer();

    public static void main(String[] args) {
        SampleProducer sampleProducer = new SampleProducer();
        //sampleProducer.sendSimpleMessageValueAtOnce();
        //sampleProducer.sendSimpleMessageKeyAndValueAtOnce();
        sampleProducer.sendFixedPartition();
    }

    public void sendSimpleMessageValueAtOnce() {
        // 레코드 생성
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "testMessage");
        producer.send(record); // 브로커로 바로 전송하지 않고 파티셔너를 통해 어커뮤레이터에 쌓아놓은뒤 배치로 한번에 전송한다
        log.info("{}", record);
        producer.flush();
        producer.close(); // 어큐뮤레이터에 저장된 데이터를 카프카 클러스터로 전송. close를 호출하지 않고 종료시 데이터가 유실된다 
    }

    public void sendSimpleMessageKeyAndValueAtOnce() {
        // 레코드 생성
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "testMessageKey","testMessage");
        ProducerRecord<String, String> record2 = new ProducerRecord<>(TOPIC_NAME, "testMessageKey2","testMessage2");

        // 전송
        producer.send(record);
        producer.send(record2);

        producer.flush();
        producer.close();
    }

    public void sendFixedPartition() {
        String key = "testMessageKey";
        String value = "testMessage";
        int partitionNo = 0;

        // 레코드 생성
        // 파티션 번호를 지정하지 않으면 파티션 키를 해시값으로 파티션을 선택한다. 파티션의 갯수가 늘어나면 다른파티션으로 들어갈수 있다.
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, 0, key, value);

        // 전송
        producer.send(record);

        producer.flush();
        producer.close();
    }

    private KafkaProducer<String, String> getProducer() {
        // 프로듀서 설정
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); // 카프카 브로커 서버 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key 직렬화 클래스
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value 직렬화 클래스
        return new KafkaProducer<>(config); // KafkaProducer<key타입, value타입>
    }
}
