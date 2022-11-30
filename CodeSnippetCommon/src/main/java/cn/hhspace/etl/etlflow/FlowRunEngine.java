package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.framework.BeanException;
import cn.hhspace.etl.framework.BeanInventory;
import cn.hhspace.etl.utils.CountedBlockQueue;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 18:01
 * @Descriptions: 记录Flow动态执行环境，运行Flow执行动作
 * FlowRunContext只保存程序执行点的信息
 * FlowRunEngine保存的信息更多，持有FlowRunContext
 */
public class FlowRunEngine implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(FlowRunEngine.class);

    private BeanInventory beanInventory;

    public BeanInventory getBeanInventory() {
        return beanInventory;
    }

    public void setBeanInventory(BeanInventory beanInventory) {
        this.beanInventory = beanInventory;
    }

    /**
     * js执行引擎
     */
    private transient ScriptEngine scriptEngine;

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    /**
     * FlowRunEngine初始化对应的流程
     */
    private String startFlowId;

    private Flow startFlow;

    public String getStartFlowId() {
        return startFlowId;
    }

    /**
     * 当前执行上下文
     */
    private FlowRunContext currentContext;

    public FlowRunContext getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(FlowRunContext currentContext) {
        this.currentContext = currentContext;
    }

    /**
     * 运行信息
     */
    private RunInfo runInfo;

    public RunInfo getRunInfo() {
        return runInfo;
    }

    public void setRunInfo(RunInfo runInfo) {
        this.runInfo = runInfo;
    }

    private boolean debugMode=false;

    /**
     *
     */
    private CountedBlockQueue<JSONArray> dataQueue;

    public CountedBlockQueue<JSONArray> getDataQueue() {
        return dataQueue;
    }

    /**
     * 停止调试时要调用此方法，结束线程，释放资源
     * @throws Exception
     */
    @Override
    public void close() {
        if (null != startFlow) {
            try {
                startFlow.close();
            } catch (Exception e) {
                logger.warn("关闭流程时发生异常", e);
            }
        }

        currentContext = null;
    }

    /**
     * Constructor
     */
    public FlowRunEngine(BeanInventory beanInventory, String flowId, CountedBlockQueue<JSONArray> dataQueue, boolean debugMode) throws Exception {
        this.beanInventory = beanInventory;
        this.startFlowId = flowId;
        this.dataQueue = dataQueue;
        this.debugMode = debugMode;

        if (!beanInventory.containsBean(startFlowId)) {
            throw new Exception("FlowRunEngine创建时找不到流程" + startFlowId);
        }

        this.startFlow = beanInventory.getBean(startFlowId, Flow.class);
    }

    public void init() {
        resetFlow();
    }

    private void resetFlow() {
        this.startFlow = beanInventory.getBean(startFlowId, Flow.class);
        if (null == startFlow) {
            throw new BeanException(ErrorCode.FLOW_NOT_FOUND, "找不到流程：" + startFlowId);
        }
        startFlow.init(this.beanInventory);
        startFlow.prepareRunEngine(this);
        startFlow.getSourceNode().setDataQueue(dataQueue);
        if (debugMode) {
            startFlow.getSourceNode().start();
        }
    }
    /**
     * 开始从源节点运行一个流程
     */
    public FlowRunContext begin() {
        currentContext = new FlowRunContext();
        currentContext.setDataIn(new JSONArray());
        currentContext.setFlow(startFlow);
        currentContext.setNode(startFlow.getSourceNode());
        currentContext.setStopReason(FlowStopReason.TRAP_BEGIN);
        return currentContext;
    }

    /**
     * 运行流程到结束
     */
    public FlowRunContext runToEnd() {
        do {
            if (null == currentContext.getNode()){
                return currentContext;
            }
            step(FlowStopReason.RUNNING);
        } while (true);
    }
    /**
     * 单步或者运行一步
     */
    public FlowRunContext step(FlowStopReason flowStopReason) {
        //初始设置FlowStopReason
        currentContext.setStopMsg("");
        currentContext.setStopReason(flowStopReason);

        Node currentNode = currentContext.getNode();

        //TODO 如果是调试模式需要保护现场,先初始化变量，暂不实现
        FlowRunContext saveContext;
        saveContext = currentContext;
        try {
            currentNode.run(this);
        } catch (Exception e) {
            //TODO 异常处理，比较复杂，需要恢复现场等
            currentContext = saveContext;
        }

        return currentContext;
    }

    /**
     * 从源节点后的下一个加工节点开始运行一个流程
     * @param data
     */
    public FlowRunContext beginProcess(JSONArray data) {
        currentContext = new FlowRunContext();
        currentContext.setDataIn(data);
        currentContext.setFlow(startFlow);
        currentContext.setNode(startFlow.getFirstProcessNode());
        currentContext.setStopReason(FlowStopReason.TRAP_BEGIN);
        return currentContext;
    }
}
