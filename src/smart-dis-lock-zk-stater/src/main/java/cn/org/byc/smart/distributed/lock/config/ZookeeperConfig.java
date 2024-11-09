package cn.org.byc.smart.distributed.lock.config;

import cn.org.byc.smart.distributed.lock.config.props.ZookeeperProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfig {

    private final ZookeeperProperties zookeeperProperties;

    @Autowired
    public ZookeeperConfig(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    /**
     * 如果你的Zookeeper版本低于3.4，使用这个https://curator.apache.org/docs/zk-compatibility-34
     * @return
     */
    @Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getTimeout(), zookeeperProperties.getRetry());
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getUrl(), retryPolicy);
        client.start();
        return client;
    }
}
