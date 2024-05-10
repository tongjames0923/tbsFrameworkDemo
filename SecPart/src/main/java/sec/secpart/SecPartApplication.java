package sec.secpart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tbs.framework.redis.annotations.EnableRedisMessageCenter;
import tbs.framework.redis.annotations.EnableTbsRedis;
import tbs.framework.sql.annotations.EnableTbsSqlUtils;

@SpringBootApplication
@EnableTbsRedis
@EnableRedisMessageCenter
@EnableTbsSqlUtils
public class SecPartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecPartApplication.class, args);
    }

}
