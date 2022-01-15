package cn.hhspace.rpcdemo.service.impl;

import cn.hhspace.rpcdemo.annotation.RpcService;
import cn.hhspace.rpcdemo.entity.InfoUser;
import cn.hhspace.rpcdemo.service.InfoUserService;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/14 1:46 下午
 * @Package: cn.hhspace.rpcdemo.service.impl
 */
@RpcService
public class InfoUserServiceImpl implements InfoUserService {

    //当做数据库，存储用户信息
    Map<String, InfoUser> infoUserMap = new HashMap<>();

    @Override
    public List<InfoUser> insertInfoUser(InfoUser infoUser) {
        System.out.println("新增用户信息：" + JSONObject.toJSONString(infoUser));
        infoUserMap.put(infoUser.getId(), infoUser);
        return getInfoUserList();
    }

    public List<InfoUser> getInfoUserList() {
        List<InfoUser> userList = new ArrayList<>();
        Iterator<Map.Entry<String, InfoUser>> iterator = infoUserMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, InfoUser> next = iterator.next();
            userList.add(next.getValue());
        }
        System.out.println("返回用户信息记录数为：" + userList.size());
        return userList;
    }

    @Override
    public InfoUser getInfoUserById(String id) {
        InfoUser infoUser = infoUserMap.get(id);
        System.out.println("查询用户ID：" + id);
        return infoUser;
    }

    @Override
    public void deleteInfoUserById(String id) {
        System.out.println("删除用户信息：" + JSONObject.toJSONString(infoUserMap.remove(id)));
    }

    @Override
    public String getNameById(String id) {
        System.out.println("根据ID查询用户名称：" + id);
        return infoUserMap.get(id).getName();
    }

    @Override
    public Map<String, InfoUser> getAllUser() {
        System.out.println("查询所有用户信息：" + infoUserMap.keySet().size());
        return infoUserMap;
    }
}
