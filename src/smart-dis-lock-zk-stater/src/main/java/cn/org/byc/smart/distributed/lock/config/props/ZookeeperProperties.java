package cn.org.byc.smart.distributed.lock.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {
    /**
     * 连接地址
     */
    private String url;

    /**
     * 超时时间(毫秒)，默认1000
     */
    private int timeout = 1000;

    /**
     * 重试次数，默认3
     */
    private int retry = 3;
}
