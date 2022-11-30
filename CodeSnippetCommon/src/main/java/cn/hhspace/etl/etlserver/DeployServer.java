package cn.hhspace.etl.etlserver;

import cn.hhspace.etl.config.DeployCfg;
import cn.hhspace.etl.deploy.Deploy;
import cn.hhspace.etl.deploy.DeployContext;
import cn.hhspace.etl.etlflow.Flow;
import cn.hhspace.etl.framework.BeanFactory;
import cn.hhspace.etl.framework.JsonFileBeanFactory;
import cn.hhspace.etl.utils.CountedBlockQueue;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 15:59
 * @Descriptions: Flow运行需要一个容器，这是一个部署容器的抽象类
 */
public abstract class DeployServer {
    /**
     * 部署容器名称
     */
    protected String name;
    /**
     * 部署的ID
     */
    protected String deployId;
    /**
     * 部署的实例
     */
    protected Deploy deploy;
    /**
     * 一次部署中包含的流程ID
     */
    protected List<String> deployFlowIds;
    /**
     * 一次部署中包含的流程
     */
    protected List<Flow> deployFlows;
    /**
     * 运行实例id
     */
    protected String runInstanceId;
    /**
     * 一次部署的上下文环境
     */
    protected DeployContext deployContext;
    /**
     * 启动容器
     */
    public abstract void start() throws Exception;
    /**
     * 停止容器
     */
    public abstract void stop();
    /**
     * 插件工厂
     */
    protected BeanFactory beanFactory;
    /**
     * 容器的状态
     */
    public abstract DeployServerStatus getServerStatus();
    /**
     * 部署配置
     */
    protected DeployCfg deployCfg;
    /**
     * 是否采用新的插件管理文件
     */
    protected boolean newBeanFactoryFlag = false;
    /**
     * 数据处理队列，从kafka获取的数据将放到这个队列里，由处理线程从队列里消费。
     */
    protected CountedBlockQueue<JSONArray> dataQueue=null;
    /**
     * 初始化容器
     */
    public void init(DeployContext context, BeanFactory inv) throws Exception {
        deployContext = context;
        deployId = context.getDeployId();
        if (null != inv) {
            beanFactory = inv;
            newBeanFactoryFlag = false;
        } else {
            JsonFileBeanFactory jfbf = new JsonFileBeanFactory();
            jfbf.setJsonConfigFileName(context.getDeployFileName());
            jfbf.init(null);
            beanFactory = jfbf;
            newBeanFactoryFlag = true;
        }
        deployContext.setBeanFactory(beanFactory);
        if (!beanFactory.containsBean(deployId)) {
            throw new Exception("DeployServer找不到部署Id:"+ deployId);
        }

        deploy = beanFactory.getBean(deployId, Deploy.class);
        deploy.setDeployServer(this);

        if (!beanFactory.containsBean(deploy.getConfigId())) {
            throw new Exception("DeployServer找不到部署配置:" + deploy.getConfigId());
        }
        deployCfg = beanFactory.getBean(deploy.getConfigId(), DeployCfg.class);

        this.name = deploy.getName();
        this.deployFlowIds = deploy.getDeployFlowIds();
        this.runInstanceId = context.getRunInstanceId();
        deployFlows = new ArrayList<>();
        for (String flowId : deployFlowIds) {
            Flow flow = beanFactory.getBean(flowId, Flow.class);
            deployFlows.add(flow);
        }
    }

    /**
     * 一个容器结束后应该调用这个方法，记录结束
     */
    protected synchronized void afterStop() {
        if (newBeanFactoryFlag && null != beanFactory) {
            beanFactory.destroy();
            beanFactory = null;
        }
        dataQueue = null;
    }

    /**
     * 返回服务器时间和偏移量，用来计算EPS。
     * 对于本地服务器，从BlockingQueue得到读取的数据量
     */
    public ProcessingOffset getOffset()  {
        if (dataQueue != null) {
            ProcessingOffset po = new ProcessingOffset();
            po.monitorTime = System.currentTimeMillis();
            long records = 0;
            for (Flow flow : deployFlows) {
                records += flow.getOutputRecs();
            }
            po.offset = records;
            return po;
        } else {
            return null;
        }
    }

}
