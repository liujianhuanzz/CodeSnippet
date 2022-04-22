package cn.hhspace.guice.jetty.exception;

import com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/22 8:28 下午
 * @Descriptions:
 */
@Provider
public class NotFoundRequestExceptionMapper implements ExceptionMapper<NotFoundRequestException> {
    @Override
    public Response toResponse(NotFoundRequestException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(ImmutableMap.of("error", e.getMessage()))
                .build();
    }
}
