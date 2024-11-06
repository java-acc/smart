package cn.org.byc.smart.log.component;

import cn.org.byc.smart.log.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SnowFlakeConfig {

    @Value("${sys.datacenterId:1}")
    private Long datacenterId;
    @Value("${sys.machineId:1}")
    private Long machineId;



    @Bean
    public SnowFlake snowFlake(){
        return new SnowFlake(datacenterId, machineId);
    }
}
