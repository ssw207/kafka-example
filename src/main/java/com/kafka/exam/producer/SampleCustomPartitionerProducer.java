package com.kafka.exam.producer;

import com.kafka.exam.producer.partitioner.SampleCustomPartitioner;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class SampleCustomPartitionerProducer {
    private final static String TOPIC_NAME = "test"; // 토픽이름
    private final static String BOOTSTRAP_SERVERS = "localhost:9095"; // 브로커 주소
    public static void main(String[] args) {
        SampleCustomPartitionerProducer sampleCustomPartitionerProducer = new SampleCustomPartitionerProducer();
        sampleCustomPartitionerProducer.send();
    }

    public void send() {
        /**
         * 메시지 키가 goToPartition1인 데이터는 커스텀 파티셔너 SampleCustomPartitioner에 의해 파티션1로 지정됨
         */
        KafkaProducer<String, String> producer = getProducer();
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "goToPartition1", "this value go to partition 1");

        producer.send(record);

        //send 실행시 바로 전송되는것이 아니라 파티셔너를 통해 어커뮤레이터에 적제되므로 flush를 호출해야 전송된다
        producer.flush();
        producer.close();
    }

    private KafkaProducer<String, String> getProducer() {
        // 프로듀서 설정
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); // 카프카 브로커 서버 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key 직렬화 클래스
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value 직렬화 클래스

        // 커스텀 파티셔너 지정. 프로듀서의 send() 호출시 해당 파티셔너를 통해 전송할 파티션을 지정한다
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SampleCustomPartitioner.class);
        return new KafkaProducer<>(config); // KafkaProducer<key타입, value타입>
    }
}
