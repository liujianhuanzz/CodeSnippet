package cn.hhspace.designpattern.observer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 15:08
 * @Descriptions:
 */
public class BaiduDataSource extends DataSource {

    @Override
    protected void updateData(String newData) {
        //如果数据发生变化，则更新数据并通知观察者
        if (!newData.equals(data)) {
            //在通知观察者之前一定要完成变化
            //必须要保持状态一致性
            data = newData;
            notifyObservers();
        }
    }

    @Override
    public void notifyObservers() {
        //广播消息，并告知观察者自己是谁
        for (Observer observer : observers) {
            observer.update(this, data);
        }
    }
}
