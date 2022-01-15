package cn.hhspace.rpcdemo.registry;

import cn.hhspace.zk.ZookeeperCachedOperator;
import cn.hhspace.zk.ZookeeperConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/14 2:28 下午
 * @Package: cn.hhspace.rpcdemo.registry
 */

@Component
public class ServiceRegistry {

    @Value("${registry.address}")
    private String registryAddress;

    private static final String ZK_REGISTRY_PATH = "/rpc";

    public void register(String data) throws Exception {
        if (data != null) {
            CuratorFramework zkClient = buildZkClient();
            if (zkClient != null) {
                Boolean exists = zkClient.checkExists().forPath(ZK_REGISTRY_PATH) != null;
                if (!exists) {
                    zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZK_REGISTRY_PATH);
                }

                zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ZK_REGISTRY_PATH + "/provider", data.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public CuratorFramework buildZkClient(){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(registryAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }
}
