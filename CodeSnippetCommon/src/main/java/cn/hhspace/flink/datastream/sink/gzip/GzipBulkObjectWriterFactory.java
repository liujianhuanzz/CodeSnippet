package cn.hhspace.flink.datastream.sink.gzip;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.core.fs.FSDataOutputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: write object to hdfs with Gzip format
 * @Date: 2022/2/16 6:46 下午
 * @Package: cn.hhspace.flink.sink.gzip
 */
public class GzipBulkObjectWriterFactory<T> implements BulkWriter.Factory<T> {
    @Override
    public BulkWriter<T> create(FSDataOutputStream fsDataOutputStream) throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fsDataOutputStream, true);
        return new GzipObjectBulkWriter<>(new ObjectOutputStream(gzipOutputStream));
    }
}
