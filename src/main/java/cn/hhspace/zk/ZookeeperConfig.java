package cn.hhspace.zk;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: zk conf
 * @Date: 2021/12/2 5:04 下午
 * @Package: cn.hhspace.zk
 */
public class ZookeeperConfig {

    private String serverList;

    private int baseSleepTimeMs;

    private int maxSleepMs;

    private int maxRetries;

    private int sessionTimeoutMs;

    private int connectionTimeoutMs;

    private String digest;

    private String dsRoot;

    private int maxWaitTime;

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxSleepMs() {
        return maxSleepMs;
    }

    public void setMaxSleepMs(int maxSleepMs) {
        this.maxSleepMs = maxSleepMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDsRoot() {
        return dsRoot;
    }

    public void setDsRoot(String dsRoot) {
        this.dsRoot = dsRoot;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
