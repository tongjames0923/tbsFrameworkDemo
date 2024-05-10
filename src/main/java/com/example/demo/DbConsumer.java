package com.example.demo;

import org.springframework.stereotype.Component;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DbConsumer implements IMessageConsumer {

    @Resource
    MessageMapper messageMapper;

    @Override
    public String consumerId() {
        return "DB-1";
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
