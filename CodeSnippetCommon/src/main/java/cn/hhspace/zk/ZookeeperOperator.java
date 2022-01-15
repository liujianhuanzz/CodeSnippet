package cn.hhspace.zk;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: zk base operator
 * @Date: 2021/12/2 4:59 下午
 * @Package: cn.hhspace.zk
 */
public class ZookeeperOperator {

    private final Logger logger = LoggerFactory.getLogger(ZookeeperOperator.class);

    private ZookeeperConfig zookeeperConfig;

    protected CuratorFramework zkClient;

    public ZookeeperOperator(ZookeeperConfig zookeeperConfig) {
        this.zookeeperConfig = zookeeperConfig;
        this.zkClient = buildClient();
        initStateListener();
    }

    /**
     * 这两个方法由子类去实现
     */
    protected void registerListener(){};
    protected void treeCacheStart(){};

    /**
     * 初始化客户端连接状态监听
     */
    public void initStateListener(){
        Preconditions.checkNotNull(zkClient);

        zkClient.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.LOST) {
                logger.error("connection lost from zookeeper");
            } else if (newState == ConnectionState.RECONNECTED) {
                logger.info("reconnected to zookeeper");
            } else if (newState == ConnectionState.SUSPENDED) {
                logger.warn("connection SUSPENDED to zookeeper");
            }
        });
    }

    /**
     * 构建zk客户端
     * @return zkClient
     */
    private CuratorFramework buildClient() {
        logger.info("zookeeper registry center init, server lists is: {}.", zookeeperConfig.getServerList());

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().ensembleProvider(new DefaultEnsembleProvider(Preconditions.checkNotNull(zookeeperConfig.getServerList(),"zookeeper quorum can't be null")))
                .retryPolicy(new ExponentialBackoffRetry(zookeeperConfig.getBaseSleepTimeMs(), zookeeperConfig.getMaxRetries(), zookeeperConfig.getMaxSleepMs()));

        //默认值
        if (0 != zookeeperConfig.getSessionTimeoutMs()) {
            builder.sessionTimeoutMs(zookeeperConfig.getSessionTimeoutMs());
        }
        if (0 != zookeeperConfig.getConnectionTimeoutMs()) {
            builder.connectionTimeoutMs(zookeeperConfig.getConnectionTimeoutMs());
        }
        if (StringUtils.isNotBlank(zookeeperConfig.getDigest())) {
            builder.authorization("digest", zookeeperConfig.getDigest().getBytes(StandardCharsets.UTF_8)).aclProvider(new ACLProvider() {

                @Override
                public List<ACL> getDefaultAcl() {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                @Override
                public List<ACL> getAclForPath(final String path) {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }
        zkClient = builder.build();
        zkClient.start();
        try {
            zkClient.blockUntilConnected();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return zkClient;
    }

    /**
     * 获取值
     * @param key
     * @return value
     */
    public String get(String key) {
        try {
            return new String(zkClient.getData().forPath(key), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.error("get key : {}", key, ex);
        }
        return null;
    }

    /**
     * 获取子节点的key
     * @param key
     * @return List<String>
     */
    public List<String> getChildrenKeys(String key) {
        try {
            return zkClient.getChildren().forPath(key);
        } catch (KeeperException.NoNodeException ex) {
            return new ArrayList<>();
        } catch (InterruptedException ex){
            logger.error("getChildrenKeys key : {} InterruptedException", key);
            throw new IllegalStateException(ex);
        } catch (Exception ex) {
            logger.error("getChildrenKeys key : {}", key, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 判断是否有子节点
     * @param key
     * @return true/false
     */
    public boolean hasChildren(String key) {
        try {
            return zkClient.checkExists().forPath(key).getNumChildren() >= 1;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 判断节点是否存在
     * @param key
     * @return true/false
     */
    public boolean isExists(String key) {
        try {
            return zkClient.checkExists().forPath(key) != null;
        } catch (Exception ex) {
            logger.error("isExisted key : {}", key, ex);
        }
        return false;
    }

    /**
     * 保存节点
     * @param key
     * @param value
     */
    public void persist(String key, String value) {
        try {
            if (!isExists(key)) {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(StandardCharsets.UTF_8));
            } else {
                update(key, value);
            }
        } catch (Exception ex) {
            logger.error("persist key : {} , value : {}", key, value, ex);
        }
    }

    /**
     * 更新节点
     * @param key
     * @param value
     */
    public void update(String key, String value) {
        try {
            zkClient.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(StandardCharsets.UTF_8)).and().commit();
        } catch (Exception ex) {
            logger.error("update key : {} , value : {}", key, value, ex);
        }
    }

    public void persistEphemeral(final String key, final String value) {
        try {
            if (isExists(key)) {
                try {
                    zkClient.delete().deletingChildrenIfNeeded().forPath(key);
                } catch (KeeperException.NoNodeException ignore) {
                    //NOP
                }
            }
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
        } catch (final Exception ex) {
            logger.error("persistEphemeral key : {} , value : {}", key, value, ex);
        }
    }

    public void persistEphemeral(String key, String value, boolean overwrite) {
        try {
            if (overwrite) {
                persistEphemeral(key, value);
            } else {
                if (!isExists(key)) {
                    zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (final Exception ex) {
            logger.error("persistEphemeral key : {} , value : {}, overwrite : {}", key, value, overwrite, ex);
        }
    }

    public void persistEphemeralSequential(final String key, String value) {
        try {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
        } catch (final Exception ex) {
            logger.error("persistEphemeralSequential key : {}", key, ex);
        }
    }

    /**
     * 移除节点
     * @param key
     */
    public void remove(final String key) {
        try {
            if (isExists(key)) {
                zkClient.delete().deletingChildrenIfNeeded().forPath(key);
            }
        } catch (KeeperException.NoNodeException ignore) {
            //NOP
        } catch (final Exception ex) {
            logger.error("remove key : {}", key, ex);
        }
    }
    public ZookeeperConfig getZookeeperConfig() {
        return zookeeperConfig;
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }

    public void close() {
        CloseableUtils.closeQuietly(zkClient);
    }
}
