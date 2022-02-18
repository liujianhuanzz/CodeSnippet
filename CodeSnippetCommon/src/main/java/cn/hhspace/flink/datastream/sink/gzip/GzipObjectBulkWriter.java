package cn.hhspace.flink.datastream.sink.gzip;

import org.apache.flink.api.common.serialization.BulkWriter;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: write object to hdfs with Gzip format
 * @Date: 2022/2/16 6:39 下午
 * @Package: cn.hhspace.flink.sink.gzip
 */
public class GzipObjectBulkWriter<T> implements BulkWriter<T> {
    private final ObjectOutputStream objectOutputStream;

    public GzipObjectBulkWriter(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void addElement(T t) throws IOException {
        objectOutputStream.writeObject(t);
    }

    @Override
    public void flush() throws IOException {
        objectOutputStream.flush();
    }

    @Override
    public void finish() throws IOException {
        objectOutputStream.close();
    }
}
