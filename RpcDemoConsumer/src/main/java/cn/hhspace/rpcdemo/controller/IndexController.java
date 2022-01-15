package cn.hhspace.rpcdemo.controller;

import cn.hhspace.rpcdemo.entity.InfoUser;
import cn.hhspace.rpcdemo.service.InfoUserService;
import cn.hhspace.utils.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 12:11 下午
 * @Package: cn.hhspace.rpcdemo.controller
 */
@Controller
public class IndexController {

    @Autowired
    InfoUserService userService;

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return new Date().toString();
    }

    @RequestMapping("insert")
    @ResponseBody
    public List<InfoUser> getUserList() throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadCount = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i=0; i < threadCount; i++) {
            new Thread(() -> {
                InfoUser infoUser = new InfoUser(IdUtil.getId(), "Jeen", "BeiJing");
                userService.insertInfoUser(infoUser);
                System.out.println("返回用户信息记录：" + JSON.toJSONString(infoUser));
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("线程数： " + threadCount + ", 执行时间：" + (end - start));
        return null;
    }

    @RequestMapping("getById")
    @ResponseBody
    public InfoUser getById(String id) {
        System.out.println("根据ID查询用户信息：" + id);
        return userService.getInfoUserById(id);
    }

    @RequestMapping("getNameById")
    @ResponseBody
    public String getNameById(String id) {
        System.out.println("根据ID查询用户名称：" + id);
        return userService.getNameById(id);
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public Map<String,InfoUser> getAllUser() throws InterruptedException {

        long start = System.currentTimeMillis();
        int threadCount = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i=0;i<threadCount;i++){
            new Thread(() -> {
                Map<String, InfoUser> allUser = userService.getAllUser();
                System.out.println("查询所有用户信息：" + JSONObject.toJSONString(allUser));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("线程数：" + threadCount + ",执行时间: " + (end-start));

        return null;
    }
}
