package cn.hhspace.flink.source;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/18 4:06 下午
 * @Package: cn.hhspace.flink.source
 */
public class UserBehaviorProducerSource extends RichParallelSourceFunction<JSONObject> {

    private static final long serialVersionUID = -1882321521306281686L;
    private boolean running = true;
    List<String> data =  new ArrayList<String>();

    @Override
    public void run(SourceContext<JSONObject> sourceContext) throws Exception {
        InputStream stream = UserBehaviorProducerSource.class.getClassLoader().getResourceAsStream("user_behavior.log");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (reader.ready()) {
            String s = reader.readLine();
            data.add(s);
        }
        reader.close();

        int SPEED = 100;
        Random random = new Random();
        ObjectMapper mapper = new ObjectMapper();
        while(running) {
            int start = random.nextInt(data.size() - SPEED);
            for (int i = start; i < start + SPEED; i++) {
                long timeInMillis = Calendar.getInstance().getTimeInMillis();
                JSONObject value = mapper.readValue(data.get(i), JSONObject.class);
                value.put("ts", timeInMillis);
                sourceContext.collect(value);
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
