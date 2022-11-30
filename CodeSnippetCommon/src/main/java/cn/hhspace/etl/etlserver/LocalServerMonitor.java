package cn.hhspace.etl.etlserver;

import cn.hhspace.etl.deploy.Deploy;
import cn.hhspace.etl.deploy.DeployContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/30 11:50
 * @Descriptions: 启动一个定时的对LocalServer的监控器，用来更新Eps等
 */
public class LocalServerMonitor implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(LocalServerMonitor.class);

    private static final Long LOCAL_SERVER_MONITOR_INTERVAL = 10000L;

    private Deploy deploy;

    private DeployContext context;

    boolean quitFlag = true;

    public LocalServerMonitor(Deploy deploy, DeployContext context) {
        this.deploy = deploy;
        this.context = context;
    }

    @Override
    public void run() {

        while (quitFlag) {
            refreshEps();
            try {
                Thread.sleep(LOCAL_SERVER_MONITOR_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void refreshEps() {
        DeployServer server = deploy.getDeployServer();
        ProcessingOffset currOffset = server.getOffset();
        long eps = 0;
        if (null != currOffset && null != context.getLastOffset()) {
            eps = (long) Math.ceil((currOffset.offset - context.getLastOffset().offset) * 1000.0 / (currOffset.monitorTime - context.getLastOffset().monitorTime));
        }
        context.setLastOffset(currOffset);
        deploy.setEps(eps);
    }

    public boolean isQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(boolean quitFlag) {
        this.quitFlag = quitFlag;
    }
}
