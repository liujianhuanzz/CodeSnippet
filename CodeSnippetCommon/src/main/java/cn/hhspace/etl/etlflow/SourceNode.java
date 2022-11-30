package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.utils.CountedBlockQueue;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 17:00
 * @Descriptions: 数据源节点
 */
public abstract class SourceNode extends Node implements Runnable {
    /**
     * 数据源就是往一个阻塞队列中放数据
     */
    @JsonIgnore
    protected CountedBlockQueue<JSONArray> dataQueue;

    @JsonIgnore
    public CountedBlockQueue<JSONArray> getDataQueue() {
        return dataQueue;
    }

    @JsonIgnore
    public void setDataQueue(CountedBlockQueue<JSONArray> dataQueue) {
        this.dataQueue = dataQueue;
    }

    /**
     * 停止标志
     */
    @JsonIgnore
    protected boolean quitFlag;

    @JsonIgnore
    public boolean isQuit() {
        return quitFlag;
    }

    /**
     * 不正常退出时候就interrupt强制退出
     */
    public void interrupt() {
        if (null != myThread) {
            myThread.interrupt();
        }
    }

    /**
     * 关闭标志
     */
    @JsonIgnore
    protected volatile boolean shutdownFlag;

    public void stop() {
        shutdownFlag = true;
    }

    @JsonIgnore
    protected Thread myThread = null;
    public synchronized void start() {
        if (null != myThread) {
            return;
        }

        myThread = new Thread(this, getId());
        shutdownFlag = false;
        myThread.start();
    }

    /**
     * run方法需要由子类进行实现，使用线程往阻塞队列放数据
     */
    @Override
    public abstract void run();

    /**
     * 通过FlowRunEngine来执行取数
     */

    public JSONArray fetchData(FlowRunEngine engine) throws InterruptedException {
        return engine.getDataQueue().poll(1000, TimeUnit.MILLISECONDS);
    }

    public void run(FlowRunEngine engine) throws InterruptedException {
        JSONArray out = fetchData(engine);
        if (null == out) {
            out = new JSONArray();
        }
        FlowRunContext currentContext = engine.getCurrentContext();
        currentContext.setNode(this.next);
        currentContext.setDataIn(out);
        engine.setCurrentContext(currentContext);
    }

    @Override
    public void close() throws Exception {
        stop();
        super.close();
    }
}
