package com.example.demo;

import org.springframework.stereotype.Component;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author abstergo
 */
@Component
public class DbConsumer implements IMessageConsumer {

    @Resource
    MessageMapper messageMapper;

    @Override
    public String consumerId() {
        return "DB-1";
    }

    public DbConsumer() {
        int a = 10;
    }

    @Override
    public Set<String> avaliableTopics() {
        return new HashSet<>(Arrays.asList("db.*"));
    }

    @Override
    public void consume(IMessage message) {
        MessageEntity entity = new MessageEntity();
        entity.setTag(message.getTag());
        entity.setWorkId(consumerId());
        entity.setMessageId(message.getMessageId());
        messageMapper.insert(entity);
    }
}
