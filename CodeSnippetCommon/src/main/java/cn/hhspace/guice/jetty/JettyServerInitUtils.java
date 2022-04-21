package cn.hhspace.guice.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import javax.ws.rs.HttpMethod;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 5:18 下午
 * @Descriptions: JettyServer初始化工具
 */
public class JettyServerInitUtils {
    private static final String[] GZIP_METHODS = new String[]{HttpMethod.GET, HttpMethod.POST};

    public static GzipHandler wrapWithDefaultGzipHandler(final Handler handler, int inflateBufferSize, int compressionLevel)
    {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setMinGzipSize(0);
        gzipHandler.setIncludedMethods(GZIP_METHODS);
        gzipHandler.setInflateBufferSize(inflateBufferSize);
        gzipHandler.setCompressionLevel(compressionLevel);

        // We don't actually have any precomputed .gz resources, and checking for them inside jars is expensive.
        gzipHandler.setCheckGzExists(false);
        gzipHandler.setHandler(handler);
        return gzipHandler;
    }
}
