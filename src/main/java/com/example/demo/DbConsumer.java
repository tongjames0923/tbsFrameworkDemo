package com.example.demo;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author abstergo
 */
@Component
public class DbConsumer implements IMessageConsumer, DisposableBean {

    @Resource
    MessageMapper messageMapper;

    private int LIMIT = 500;

    List<MessageEntity> messageEntities = new ArrayList<>(LIMIT);

    @AutoLogger
    ILogger logger;

    @Override
    public String consumerId() {
        return "DB-1";
    }

    @Override
    public Set<String> avaliableTopics() {
        return new HashSet<>(Arrays.asList("db.*", "db.#"));
    }

    @Override
    public void consume(IMessage message) {

        MessageEntity entity = new MessageEntity();
        entity.setTag(message.getTag());
        entity.setWorkId(consumerId());
        entity.setMessageId(message.getMessageId());

        synchronized (this) {
            logger.debug("{} add {}", consumerId(), entity);
            messageEntities.add(entity);
            if (messageEntities.size() % LIMIT == 0) {
                messageMapper.insertList(messageEntities);
                messageEntities.clear();
                logger.debug("{} flush {}", consumerId(), messageEntities);
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        synchronized (this) {
            messageMapper.insertList(messageEntities);
            logger.debug("{} flush {}", consumerId(), messageEntities);
        }

    }
}
