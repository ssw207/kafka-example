package com.kafka.exam.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Created by seungwoo.song on 2022-08-29
 */
@Slf4j
public class SampleRebalanceListener implements ConsumerRebalanceListener {

	// 파티선이 할당되기전 호출
	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		log.warn("파티션 할당 :{}", partitions);
	}

	// 파티션이 해제되기전 호출
	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		log.warn("파티션 해제 :{}", partitions);
	}
}
