package cn.hhspace.etl.etlserver;

import cn.hhspace.etl.common.Constants;
import cn.hhspace.etl.deploy.Deploy;
import cn.hhspace.etl.etlflow.Flow;
import cn.hhspace.etl.etlflow.FlowStatus;
import cn.hhspace.etl.etlflow.RunInfo;
import cn.hhspace.etl.etlflow.SourceNode;
import cn.hhspace.etl.etlflow.nodes.BaseKafkaTopicSrc;
import cn.hhspace.etl.utils.CountedBlockQueue;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 20:03
 * @Descriptions: 一个具体的部署容器的本地实现，采用线程池完成所有flow的解析和处理
 */
public class LocalEtlServer extends DeployServer{
    private static final Logger logger = LoggerFactory.getLogger(LocalEtlServer.class);

    private int threads;

    private int kafkaThreads;

    private int queueSize;

    private RunInfo runInfo;

    private List<BaseKafkaTopicSrc> combinedTopicSrcList;

    private Flow emptyFlow;

    private List<CombineFlowRunner> flowRunners;

    private LocalServerMonitor monitor;

    private void prepareServer() throws Exception {
        runInfo = new RunInfo();
        runInfo.setDeployId(deployId);
        runInfo.setRunInstanceId(runInstanceId);

        String threadsStr = deployCfg.getParamMap().get("threads");
        if (null == threadsStr) {
            throw new Exception("配置里没有threads参数，configId=" + deployCfg.getId());
        }
        try {
            threads = Integer.parseInt(threadsStr);
        } catch (NumberFormatException nfe) {
            throw new Exception("配置里threads不是数字，configId=" + deployCfg.getId());
        }

        String kafkaThreadsStr = deployCfg.getParamMap().get("KafkaThreads");
        if (null == kafkaThreadsStr) {
            throw new Exception("配置里没有kafkaThreads参数，configId=" + deployCfg.getId());
        }
        try {
            kafkaThreads = Integer.parseInt(kafkaThreadsStr);
        } catch (NumberFormatException ne) {
            throw new Exception("配置里KafkaThreads不是数字，configId=" + deployCfg.getId());
        }

        String queueSizeStr=deployCfg.getParamMap().get("queueSize");
        if (queueSizeStr ==null) {
            throw new Exception("配置里没有QueueSize参数，configId=" + deployCfg.getId());
        }
        try {
            queueSize=Integer.parseInt(queueSizeStr);
        } catch (NumberFormatException ne) {
            throw new Exception("配置里QueueSize不是数字，configId=" + deployCfg.getId());
        }
        dataQueue=new CountedBlockQueue<>(new LinkedBlockingQueue<>(queueSize));

        Map<String,String> topicPortFlowIdMap=new HashMap<>();
        List<String> kafkaFlowIds = new ArrayList<String>();
        String kafkaConfigId = null;
        String groupId = deployCfg.getParamMap().get(Constants.DEPLOY_CONSUMER_GROUP_ID);
        for (String flowId : deployFlowIds) {
            Flow flow = beanFactory.getBean(flowId, Flow.class);
            SourceNode sourceNode = flow.findSourceNode();
            if (sourceNode instanceof BaseKafkaTopicSrc) {
                BaseKafkaTopicSrc kafkaTopicSrc = (BaseKafkaTopicSrc) sourceNode;
                kafkaConfigId = kafkaTopicSrc.kafkaConfigId;
                kafkaFlowIds.add(flowId);
                kafkaTopicSrc.setDeployGroupId(groupId);
            }
        }

        CombineTopics combineTopics;
        if (null != kafkaConfigId) {
            combineTopics = new CombineTopics(beanFactory, kafkaConfigId, kafkaFlowIds);
            topicPortFlowIdMap.putAll(combineTopics.getTopicFlowIdMap());
            //创建一组KafkaTopicSrc节点，每个节点订阅的topic都是combineTopics
            combinedTopicSrcList = new ArrayList<>();
            for (int i = 0; i < kafkaThreads; ++i) {
                /**
                 * 把所有发布的Flow的KafkaTopic类型的SourceNode合成一个
                 * 一个SourceNode是有一个独立线程的，kafka有几个线程去读取就添加几个源节点
                 */
                BaseKafkaTopicSrc combineTopicSrc = new BaseKafkaTopicSrc();
                combineTopicSrc.setId("CombinedKafkaTopic");
                combineTopicSrc.setPluginType("KafkaTopicSrc");
                combineTopicSrc.setTopics(combineTopics.getTopics());
                combineTopicSrc.kafkaConfigId = kafkaConfigId;
                combineTopicSrc.setDataQueue(dataQueue);
                combinedTopicSrcList.add(combineTopicSrc);
            }

            //初始化KafkaTopicSrc节点时，必须有一个Flow，所以把所有的KafkaTopicSrc节点都加到一个空的Flow里边
            emptyFlow = new Flow();
            emptyFlow.setStatus(FlowStatus.RUNNING);
            emptyFlow.nodes = new ArrayList<>();
            emptyFlow.edges = new ArrayList<>();
            emptyFlow.nodes.addAll(combinedTopicSrcList);
            emptyFlow.init(beanFactory);
        }

        flowRunners = new ArrayList<>();
        for (int i = 0; i < threads; ++i) {
            CombineFlowRunner runner = new CombineFlowRunner(beanFactory, topicPortFlowIdMap, dataQueue, runInfo);
            flowRunners.add(runner);
        }

        monitor = new LocalServerMonitor(deploy, deployContext);
    }

    private DeployServerStatus serverStatus;

    @Override
    public DeployServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(DeployServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    private ThreadGroup threadGroup;

    private List<Thread> workerThreads;

    @Override
    public void start() throws Exception {
        serverStatus = DeployServerStatus.STATUS_STARTING;
        prepareServer();
        if (null != combinedTopicSrcList) {
            for (BaseKafkaTopicSrc combinedTopicSrc : combinedTopicSrcList) {
                combinedTopicSrc.start();
            }
        }

        threadGroup = new ThreadGroup("FlowRunner");
        workerThreads = new ArrayList<>();
        int runnerNumber = 0;
        for (CombineFlowRunner runner : flowRunners) {
            Thread thread = new Thread(threadGroup, runner, threadGroup.getName() + "-" + (runnerNumber++));
            workerThreads.add(thread);
            thread.start();
        }

        Thread monitorThread = new Thread(threadGroup, monitor, threadGroup.getName() + "-" + "LocalServerMonitor");
        monitorThread.start();

        serverStatus = DeployServerStatus.STATUS_RUNNING;
        logger.info("LocalEtlServer 开始多线程，线程数：" + threadGroup.activeCount());
    }

    @Override
    public void stop() {
        serverStatus = DeployServerStatus.STATUS_STOPPING;
        //关闭数据源节点，不要取数据了
        logger.info("LocalEtlServer 关闭数据源节点");
        if (null != combinedTopicSrcList) {
            for (BaseKafkaTopicSrc combinedTopicSrc : combinedTopicSrcList) {
                combinedTopicSrc.stop();
            }
        }

        try {
            Thread.sleep(Constants.KAFKA_CLOSE_WAIT_SECONDS * 1000L);
        } catch (InterruptedException e) {
            logger.error("stop() InterruptedException",e);
            Thread.currentThread().interrupt();
        }

        //等待一段时间，让数据队列变空再停处理流程，不然会有数据丢失
        logger.info("LocalEtlServer 关闭Runners");
        for (CombineFlowRunner runner : flowRunners){
            runner.stop();
        }

        if (null != combinedTopicSrcList) {
            for (BaseKafkaTopicSrc combinedTopicSrc : combinedTopicSrcList) {
                if (!combinedTopicSrc.isQuit()){
                    logger.warn("数据源节点等待" + Constants.KAFKA_CLOSE_WAIT_SECONDS + "秒后未正常退出，强制中断此线程。");
                    combinedTopicSrc.interrupt();
                }
            }
            logger.info("LocalEtlServer 等待KafkaTopicSrc 节点的线程结束");
        }

        logger.info("LocalEtlServer等待Runner的线程结束");
        int time = 1000;
        int i = 0;
        while (1 < Constants.SERVER_WAIT_MAX_TIME) {
            ThreadUtil.safeSleep(time);
            if (isAllRunnerQuit()){
                break;
            }
            i++;
        }

        logger.info("LocalEtlServer等待Runner的线程结束时间{}s", i);
        // 最大等待时间后仍存在线程存活时，主动关闭
        if (Constants.SERVER_WAIT_MAX_TIME == i) {
            for (Thread workThread : workerThreads) {
                if (workThread.isAlive()){
                    logger.warn("work thread 仍未结束，强制中断. 可能需要减少服务器配置中的数据队列长度。thread name: {}, stack trace: {}", workThread.getName(), workThread.getStackTrace()[0]);
                    workThread.interrupt();
                }
            }
        }

        logger.info("LocalEtlServer全部线程结束");
        serverStatus=DeployServerStatus.STATUS_STOP;

        logger.info("LocalEtlServer销毁所有runner");
        for (CombineFlowRunner runner : flowRunners) {
            runner.destroy();
        }

        logger.info("LocalEtlServer销毁Monitor线程");
        monitor.setQuitFlag(false);
        monitor = null;

        afterStop();
        try {
            if (emptyFlow != null) {
                emptyFlow.close();
            }
            emptyFlow = null;
        } catch (Exception e) {
            logger.error("destory emptyFlow error",e);
        }

        combinedTopicSrcList=null;
        threadGroup=null;
        flowRunners =null;
    }

    private boolean isAllRunnerQuit() {
        boolean flag = true;
        for (CombineFlowRunner runner : flowRunners) {
            if (null != runner && !BooleanUtil.isTrue(runner.isQuitFlag())){
                flag = false;
                break;
            }
        }
        return flag;
    }
}
