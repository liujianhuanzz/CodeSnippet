package cn.hhspace.etl.deploy;

import cn.hhspace.etl.etlserver.ProcessingOffset;
import cn.hhspace.etl.framework.BeanFactory;

import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 16:11
 * @Descriptions: 一次部署的上下文
 */
public class DeployContext {
    /**
     * 部署ID
     */
    private String deployId;
    /**
     * 运行实例ID
     */
    private String runInstanceId;
    /**
     * 部署使用的json文件
     */
    private String deployFileName;
    /**
     * 部署的时间
     */
    private long deployTime;
    /**
     * 部署中包含的插件ID列表
     */
    private Set<String> deployBeanIds;
    /**
     * 部署容器的ID，为了限制一个容器运行一个部署
     */
    private String deployServerId;
    /**
     * 记录运行时上一次的offset，用来计算eps
     */
    private ProcessingOffset lastOffset = null;
    /**
     * 插件工厂
     */
    private BeanFactory beanFactory;


    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getRunInstanceId() {
        return runInstanceId;
    }

    public void setRunInstanceId(String runInstanceId) {
        this.runInstanceId = runInstanceId;
    }

    public String getDeployFileName() {
        return deployFileName;
    }

    public void setDeployFileName(String deployFileName) {
        this.deployFileName = deployFileName;
    }

    public long getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(long deployTime) {
        this.deployTime = deployTime;
    }

    public Set<String> getDeployBeanIds() {
        return deployBeanIds;
    }

    public void setDeployBeanIds(Set<String> deployBeanIds) {
        this.deployBeanIds = deployBeanIds;
    }

    public String getDeployServerId() {
        return deployServerId;
    }

    public void setDeployServerId(String deployServerId) {
        this.deployServerId = deployServerId;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ProcessingOffset getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(ProcessingOffset lastOffset) {
        this.lastOffset = lastOffset;
    }
}
