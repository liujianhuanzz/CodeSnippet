package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.framework.JsonFileBeanFactory;
import cn.hhspace.etl.utils.CountedBlockQueue;
import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/18 15:28
 * @Descriptions:
 */
public class FlowRunTest {
    JsonFileBeanFactory beanFactory;

    @Before
    public void prepare() throws Exception {
        beanFactory = new JsonFileBeanFactory();
        beanFactory.setJsonConfigFileName("FlowTest.json");
        beanFactory.init(null);
    }

    @Test
    public void runKafkaInputFlow() throws Exception {
        Flow flow = beanFactory.getBean("Flow_5Y5154", Flow.class);
        flow.setStatus(FlowStatus.RUNNING);
        CountedBlockQueue<JSONArray> dataQueue = new CountedBlockQueue<>(new LinkedBlockingQueue<>(10000));
        FlowRunEngine engine = new FlowRunEngine(beanFactory, flow.getId(), dataQueue, true);
        engine.init();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 10 * 3600) {
            engine.begin();
            engine.runToEnd();
        }
        System.out.println(flow.getOutputRecs());
    }
}
