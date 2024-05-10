package sec.secpart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.impls.SimpleMessageQueue;
import tbs.framework.redis.impls.AbstractRedisMessageCenter;
import tbs.framework.redis.impls.RedisMessageReceiver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            public void consume(IMessage message) {
                getLog().info("consume msg: {}", message.toString());
                message.setConsumed();
            }
        };
    }

    @Bean
    AbstractRedisMessageCenter messageCenter(RedisMessageReceiver receiver) {
        return new AbstractRedisMessageCenter(receiver) {
            @Override
            public void onMessageSent(IMessage message) {
                getLog().info("Sent message: " + message);
            }

            @Override
            public boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
                IMessageConsumer consumer) {
                getLog().info("Failed message: " + message);
                return false;
            }
        };
    }
}
