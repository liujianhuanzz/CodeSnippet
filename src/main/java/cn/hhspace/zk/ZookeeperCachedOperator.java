package cn.hhspace.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: cached zk operator
 * @Date: 2021/12/2 5:59 下午
 * @Package: cn.hhspace.zk
 */
public class ZookeeperCachedOperator extends ZookeeperOperator{

    private final Logger logger = LoggerFactory.getLogger(ZookeeperCachedOperator.class);

    private TreeCache treeCache;

    public ZookeeperCachedOperator(ZookeeperConfig zookeeperConfig) {
        super(zookeeperConfig);
    }

    @Override
    protected void registerListener() {
        treeCache.getListenable().addListener((client, event) -> {
            String path = null == event.getData() ? "" : event.getData().getPath();
            if (path.isEmpty()) {
                return;
            }
            dataChanged(client, event, path);
        });
    }

    @Override
    protected void treeCacheStart() {
        treeCache = new TreeCache(zkClient, getZookeeperConfig().getDsRoot() + "/nodes");
        logger.info("add listener to zk path: {}", getZookeeperConfig().getDsRoot());
        try {
            treeCache.start();
        } catch (Exception e) {
            logger.error("add listener to zk path: {} failed", getZookeeperConfig().getDsRoot());
            throw new RuntimeException(e);
        }
    }

    /**
     * 这个方法留给子类实现，定义当监听到数据发生变化后要干什么
     * @param client
     * @param event
     * @param path
     */
    protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {};

    public String getFromCache(String key) {
        ChildData resultInCache = treeCache.getCurrentData(key);
        if (null != resultInCache) {
            return null == resultInCache.getData() ? null : new String(resultInCache.getData(), StandardCharsets.UTF_8);
        }
        return null;
    }

    public TreeCache getTreeCache() {
        return treeCache;
    }

    public void addListener(TreeCacheListener listener) {
        this.treeCache.getListenable().addListener(listener);
    }

    @Override
    public void close() {
        treeCache.close();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {

        }
        super.close();
    }
}
