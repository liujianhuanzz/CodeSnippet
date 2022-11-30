package cn.hhspace.etl.etlserver;

import cn.hhspace.etl.etlflow.FlowRunEngine;
import cn.hhspace.etl.etlflow.RunInfo;
import cn.hhspace.etl.framework.BeanInventory;
import cn.hhspace.etl.utils.CountedBlockQueue;
import cn.hhspace.etl.utils.JSONMate;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/22 19:35
 * @Descriptions:
 */
public class CombineFlowRunner implements Runnable, IProcessCount{

    private static final Logger logger = LoggerFactory.getLogger(CombineFlowRunner.class);

    /**
     * topic和FlowRunEngine映射表
     */
    private Map<String, FlowRunEngine> flowRunEngineMap = new HashMap<String, FlowRunEngine>();
    /**
     * 处理数据的queue引用
     */
    private CountedBlockQueue<JSONArray> dataQueue;
    /**
     * 处理记录数
     */
    protected long processCount = 0;

    public CombineFlowRunner(BeanInventory beanInventory, Map<String, String> topicFlowIdMap, CountedBlockQueue<JSONArray> dataQueue, RunInfo runInfo) throws Exception {
        this.dataQueue = dataQueue;

        for (Map.Entry<String, String> entry : topicFlowIdMap.entrySet()) {
            String topicOrPort = entry.getKey();
            String flowId = entry.getValue();
            FlowRunEngine engine = new FlowRunEngine(beanInventory, flowId, dataQueue, false);
            engine.setRunInfo(runInfo);
            flowRunEngineMap.put(topicOrPort, engine);
            engine.init();
        }
    }

    @JsonIgnore
    protected volatile boolean quitFlag = false;

    public boolean isQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(boolean quitFlag) {
        this.quitFlag = quitFlag;
    }

    @JsonIgnore
    protected volatile boolean shutdownFlag = false;

    public boolean isShutdownFlag() {
        return shutdownFlag;
    }

    public void setShutdownFlag(boolean shutdownFlag) {
        this.shutdownFlag = shutdownFlag;
    }

    public void stop() {
        setShutdownFlag(true);
    }

    @Override
    public long getProcessCount() {
        return processCount;
    }

    @Override
    public void run() {
        try {
            do {
                try {
                    JSONArray in = dataQueue.poll(100, TimeUnit.MILLISECONDS);
                    process(in);
                } catch (InterruptedException ie) {
                    logger.error("CombineKafkaFlowRunner run() InterruptedException", ie);
                    Thread.currentThread().interrupt();
                }
            } while (!shutdownFlag);
        } catch (Error e) {
            System.exit(2);
        }

        for (FlowRunEngine engine : flowRunEngineMap.values()) {
            engine.close();
        }
        quitFlag = true;
    }

    public void process(JSONArray in) {
        if (null != in && !in.isEmpty()) {
            int count = in.size();
            processCount += count;
            JSONObject inObj = in.getJSONObject(0);
            String topic = (String) JSONMate.getField(inObj, "_in.topic");
            if (null == topic) {
                throw new RuntimeException("传入对象的_in.topic为null");
            }
            FlowRunEngine engine = flowRunEngineMap.get(topic);
            try {
                engine.beginProcess(in);
                engine.runToEnd();
            } catch (Throwable e) {
                if (e instanceof Error) {
                    logger.error("发现致命错误，解析Job异常中止，退出整个应用。",e);
                    throw e;
                    //System.exit(2);  //如果是严重问题，需要强制退出部署App。
                } else {
                    logger.error("运行flowRunEngine时报错", e);
                }
            }
        }
    }

    public void destroy() {
        for (FlowRunEngine engine : flowRunEngineMap.values()) {
            engine.close();
        }
        quitFlag = true;
    }
}
