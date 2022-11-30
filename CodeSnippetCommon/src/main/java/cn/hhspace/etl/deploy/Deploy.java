package cn.hhspace.etl.deploy;

import cn.hhspace.etl.etlserver.DeployServer;
import cn.hhspace.etl.etlserver.DeployServerStatus;
import cn.hhspace.etl.framework.BeanInventory;
import cn.hhspace.etl.framework.LifeCycleIntf;
import cn.hhspace.etl.framework.NamedPlugin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 16:50
 * @Descriptions: 一个部署插件，这里的部署是一个名称，指的将N个流程进行发布
 */
@JsonPropertyOrder({"id","pluginType","name","configId","deployFlowIds"})
public class Deploy implements NamedPlugin, LifeCycleIntf {

    private static final Logger logger = LoggerFactory.getLogger(Deploy.class);
    /**
     * 一次部署的状态
     */
    public static final int STATUS_STOP=0;
    public static final int STATUS_RUNNING=1;
    public static final int STATUS_STOPPING=2;
    public static final int STATUS_NEED_RESTART=3;
    public static final int STATUS_STOP_DATA=4;
    public static final int STATUS_STARTING=5;

    /**
     * 部署ID
     */
    protected String id;
    /**
     * 插件类型
     */
    protected String pluginType;
    /**
     * 部署名称
     */
    private String name;
    /**
     * 引用的配置ID
     */
    private String configId;
    /**
     * 一次部署包含的Flow
     */
    private List<String> deployFlowIds;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 公开
     */
    private boolean isPublic;
    /**
     * 部署所在的容器
     */
    private DeployServer deployServer;

    /**
     * 标识部署启动是否完成
     */
    @JsonIgnore
    private boolean isStarted;

    /**
     * 是否从前台停止。如果前台停止的，必须前台才能启动
     */
    @JsonIgnore
    private boolean manualStopped=false;

    /**
     * 部署时间
     */
    private long deployTime;

    /**
     * 按流程统计EPS
     */
    @JsonIgnore
    private Map<String, Long> flowEps;
    /**
     * 部署维度的EPS
     */
    private long eps = 0;

    /**
     * 状态code
     */
    private int status;

    @Override
    public void init(BeanInventory inv) throws Exception {
        //do nothing
    }

    @Override
    public void destroy() throws Exception {
        //do nothing
    }

    @Override
    public void start() throws Exception {
        //do nothing
        //应该由一个运行服务来启动运行server
    }

    @Override
    public void stop() throws Exception {
        if (null != getDeployServer()) {
            int status = this.getStatus();
            if (status == STATUS_RUNNING) {
                throw new RuntimeException("部署还在启动中，需等待部署启动完成，才能执行停止命令");
            }

            DeployServer deployServerOld = this.getDeployServer();
            this.setDeployServer(null);
            this.setStatus(STATUS_STOPPING);
            try {
                deployServerOld.stop();
            } catch (Exception e) {
                logger.error("停止服务器时报错", e);
            }

            this.setStatus(STATUS_STOP);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning(true);
    }

    @JsonIgnore
    public boolean isRunning(boolean updateStatus) {
        if (null == deployServer) {
            return false;
        }

        DeployServerStatus serverStatus = deployServer.getServerStatus();
        if (STATUS_NEED_RESTART != this.status &&
                STATUS_STOP_DATA != this.status &&
                DeployServerStatus.STATUS_UNKNOWN.getValue() != this.status &&
                updateStatus
        ) {
            this.status = serverStatus.getValue();
        }

        switch (serverStatus) {
            case STATUS_STOP:
            case STATUS_UNKNOWN:
                return false;
            case STATUS_STOPPING:
            case STATUS_RUNNING:
            case STATUS_STARTING:
                return true;
            default:
                throw new RuntimeException("服务器返回未知状态: " + serverStatus);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPluginType() {
        return pluginType;
    }

    @Override
    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    @Override
    public Collection<String> referPluginIds() {
        List<String> referIds = new ArrayList<>();
        referIds.addAll(getDeployFlowIds());
        referIds.add(getConfigId());
        return referIds;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public List<String> getDeployFlowIds() {
        return deployFlowIds;
    }

    public void setDeployFlowIds(List<String> deployFlowIds) {
        this.deployFlowIds = deployFlowIds;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public DeployServer getDeployServer() {
        return deployServer;
    }

    public void setDeployServer(DeployServer deployServer) {
        this.deployServer = deployServer;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isManualStopped() {
        return manualStopped;
    }

    public void setManualStopped(boolean manualStopped) {
        this.manualStopped = manualStopped;
    }

    public long getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(long deployTime) {
        this.deployTime = deployTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Long> getFlowEps() {
        return flowEps;
    }

    public void setFlowEps(Map<String, Long> flowEps) {
        this.flowEps = flowEps;
    }

    public long getEps() {
        return eps;
    }

    public void setEps(long eps) {
        this.eps = eps;
    }

    public void clearEps() {
        this.eps = 0;
        this.flowEps = null;
    }
}
