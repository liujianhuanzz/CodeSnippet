package cn.hhspace.guice.jetty.exception;

import com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 7:37 下午
 * @Descriptions:
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ImmutableMap.of("error", e.getMessage()))
                .build();
    }
}
