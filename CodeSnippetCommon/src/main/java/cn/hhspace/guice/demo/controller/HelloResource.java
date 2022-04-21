package cn.hhspace.guice.demo.controller;

import cn.hhspace.guice.mapbinder.annotations.LazySingleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 7:28 下午
 * @Descriptions:
 */
@LazySingleton
@Path("/hello")
public class HelloResource {

    @GET
    @Path("/guice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloGuice() {
        return Response.ok("Hello Guice").build();
    }

    @GET
    @Path("/jersey")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloJersey() {
        return Response.ok("Hello Jersey").build();
    }
}
