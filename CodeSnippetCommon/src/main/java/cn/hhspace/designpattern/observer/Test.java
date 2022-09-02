package cn.hhspace.designpattern.observer;

import java.util.Date;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 15:38
 * @Descriptions:
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        View view = new View();
        view.show("初始状态");

        BaiduDataSource baiduDataSource = new BaiduDataSource();
        GoogleDataSource googleDataSource = new GoogleDataSource();

        ObserverA observerA = new ObserverA(view);

        baiduDataSource.addObserver(observerA);
        googleDataSource.addObserver(observerA);

        while (true) {
            baiduDataSource.updateData("这是百度新数据————" + new Date());
            googleDataSource.updateData("这是谷歌新数据————" + new Date());
            System.out.println();
            Thread.sleep(10000);
        }
    }
}
