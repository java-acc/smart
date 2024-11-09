package cn.org.byc.smart.distributed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SmartDisLockZkStaterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartDisLockZkStaterApplication.class, args);
    }

}
