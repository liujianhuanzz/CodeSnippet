package cn.hhspace.flink.source;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Calendar;
import java.util.Random;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:07 下午
 * @Package: cn.hhspace.flink.source
 */
public class SensorSource extends RichParallelSourceFunction<SensorReading> {

    private static final long serialVersionUID = 4049262408264706429L;
    private boolean running = true;

    @Override
    public void run(SourceContext<SensorReading> sourceContext) throws Exception {
        Random rand = new Random();

        int taskIdx = this.getRuntimeContext().getIndexOfThisSubtask();

        String[] sensorIds = new String[10];
        double[] currFTemp = new double[10];

        for (int i=0; i < 10; i++) {
            sensorIds[i] = "sensor_" + (taskIdx * 10 + i);
            currFTemp[i] = 65 + (rand.nextGaussian() * 20);
        }

        while (running) {
            long curTime = Calendar.getInstance().getTimeInMillis();

            for (int i = 0; i < 10; i++) {
                currFTemp[i] += rand.nextGaussian() * 0.5;
                sourceContext.collect(new SensorReading(sensorIds[i], curTime, currFTemp[i]));
            }

            Thread.sleep(100);
        }
    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
