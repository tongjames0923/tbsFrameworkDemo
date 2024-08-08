package sec.secpart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class Config {

    ILogger l;

    private ILogger getLog() {
        if (l == null) {
            l = LogFactory.getInstance().getLogger(this.getClass().getName());
        }
        return l;
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
            }
        };
    }

}
