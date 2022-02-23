package cn.hhspace.flink.sink.gzip;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.core.fs.FSDataOutputStream;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: write string to hdfs with Gzip format
 * @Date: 2022/2/16 6:43 下午
 * @Package: cn.hhspace.flink.sink.gzip
 */
public class GzipBulkStringWriterFactory<T> implements BulkWriter.Factory<T> {
    @Override
    public BulkWriter<T> create(FSDataOutputStream fsDataOutputStream) throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fsDataOutputStream, true);
        return new GzipStringBulkWriter<>(gzipOutputStream);
    }
}
