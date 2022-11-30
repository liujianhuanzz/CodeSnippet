package cn.hhspace.zk;

import org.apache.curator.ensemble.EnsembleProvider;

import java.io.IOException;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 默认的配置Provider
 * @Date: 2021/12/2 5:14 下午
 * @Package: cn.hhspace.zk
 */
public class DefaultEnsembleProvider implements EnsembleProvider {

    private final String serverList;

    public DefaultEnsembleProvider(String serverList) {
        this.serverList = serverList;
    }

    @Override
    public void start() throws Exception {
        //do nothing
    }

    @Override
    public String getConnectionString() {
        return serverList;
    }

    @Override
    public void close() throws IOException {
        //do nothing
    }

    public void setConnectionString(String s) {
        //do nothing
    }

    public boolean updateServerListEnabled() {
        return false;
    }
}
