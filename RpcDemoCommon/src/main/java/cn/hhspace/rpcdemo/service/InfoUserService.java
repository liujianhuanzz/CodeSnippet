package cn.hhspace.rpcdemo.service;

import cn.hhspace.rpcdemo.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 定义一个操作用户信息的服务
 * @Date: 2022/1/14 1:35 下午
 * @Package: cn.hhspace.rpcdemo.service
 */
public interface InfoUserService {

    List<InfoUser> insertInfoUser(InfoUser infoUser);

    InfoUser getInfoUserById(String id);

    void deleteInfoUserById(String id);

    String getNameById(String id);

    Map<String, InfoUser> getAllUser();
}
