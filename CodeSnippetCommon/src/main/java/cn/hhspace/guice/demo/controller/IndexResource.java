package cn.hhspace.guice.demo.controller;

import cn.hhspace.guice.mapbinder.annotations.LazySingleton;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 4:15 下午
 * @Descriptions:
 */
@LazySingleton
@Path("/index")
public class IndexResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(
            @Context final HttpServletRequest req
    )
    {
        return Response.ok("Index Test").build();
    }
}
