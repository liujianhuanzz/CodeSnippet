package cn.hhspace.rpcdemo.connection;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 3:03 下午
 * @Package: cn.hhspace.rpcdemo.connection
 */
@Component
public class ServiceDiscovery {

    @Value("${registry.address}")
    private String registryAddress;

    @Autowired
    ConnectManage connectManage;

    // 服务地址列表
    private volatile List<String> addressList = new ArrayList<>();
    private static final String ZK_REGISTRY_PATH = "/rpc";
    private CuratorFramework zkClient;

    @PostConstruct
    public void init() throws Exception {
        zkClient = connectServer();
        if (zkClient != null) {
            watchNode(zkClient);
        }
    }

    private CuratorFramework connectServer() {
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

    private void watchNode(CuratorFramework zkClient) throws Exception {
        TreeCache treeCache = new TreeCache(zkClient, ZK_REGISTRY_PATH);
        treeCache.getListenable().addListener((client, event) -> {
            String path = null == event.getData() ? "" : event.getData().getPath();
            if (path.isEmpty()) {
                return;
            }
            System.out.println("监听到节点发生变化：" + path);
            List<String> nodes = zkClient.getChildren().forPath(ZK_REGISTRY_PATH);
            getNodeData(nodes);
            connectManage.updateConnectServer(addressList);
        });
        treeCache.start();

        getNodeData(zkClient.getChildren().forPath(ZK_REGISTRY_PATH));
        connectManage.updateConnectServer(addressList);
    }

    private void getNodeData(List<String> nodes) throws Exception {
        System.out.println("/rpc子节点数据为: " + JSONObject.toJSONString(nodes));
        for(String node:nodes){
            byte[] bytes = zkClient.getData().forPath(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
