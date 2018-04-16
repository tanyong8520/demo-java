package com.tany.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component("TanyKafkaConsumerListener")
public class KafkaConsumerListener {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "${kafka.data.topic.engine}")
    public void engineListen(ConsumerRecord<?, ?> record) {
        logger.info("获取kafka测试数据,key: {},value:{}" ,record.key(),record.value());
    }
}
