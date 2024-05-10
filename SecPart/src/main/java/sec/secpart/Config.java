package sec.secpart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.*;
import tbs.framework.mq.impls.queue.SimpleMessageQueue;
import tbs.framework.redis.impls.RedisMessageCenter;
import tbs.framework.redis.impls.RedisMessageReceiver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

@Configuration
public class Config {

    ILogger l;

    private ILogger getLog() {
        if (l == null) {
            l = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return l;
    }

    @Bean
    public IMessageQueue messageQueue() {
        return new SimpleMessageQueue();
    }

    @Bean
    RedisMessageReceiver messageReceiver(RedisMessageListenerContainer container, IMessageQueue messageQueue) {
        return new RedisMessageReceiver(container, messageQueue);
    }

    @Bean
    IMessageConsumer messageConsumer() {
        return new IMessageConsumer() {
            @Override
            public String consumerId() {
                return "anOther";
            }

            @Override
            public Set<String> avaliableTopics() {
                return new HashSet<>(Arrays.asList("core", "优先级"));
            }

            @Override
            public boolean consume(IMessage message) {
                getLog().info("consume msg: {}", message.toString());
                return true;
            }
        };
    }

    @Bean
    RedisMessageCenter messageCenter(RedisMessageReceiver receiver, IMessageQueue messageQueue,
        IMessageQueueEvents events, IMessageConsumerManager consumerManager) {
        return new RedisMessageCenter(receiver, consumerManager, events,
            Executors.newCachedThreadPool(new CustomizableThreadFactory("msg-center")));
    }
}
