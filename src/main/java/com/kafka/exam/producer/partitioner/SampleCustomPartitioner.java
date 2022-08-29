package com.kafka.exam.producer.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 특정 데이터를 가지는 레코드는 특정 파티션으로 보내야하는 경우 커스텀 파티셔너를 이용해서 파티션을 지정할수 있다.
 */
public class SampleCustomPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        if (keyBytes == null) {
            throw new InvalidRecordException("need message key");
        }

        // 메시지 키에 goToPartition1 값이 있으면 파티션 0으로 전송
        if (((String)key).equals("goToPartition1")) {
            return 0;
        }

        // 나머지는 키를 해시값으로 보낼 파티션을 결정한다
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        return Utils.toPositive(Utils.murmur2(keyBytes) % numPartitions);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
