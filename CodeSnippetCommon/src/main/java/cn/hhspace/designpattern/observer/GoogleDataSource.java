package cn.hhspace.designpattern.observer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 15:22
 * @Descriptions:
 */
public class GoogleDataSource extends DataSource {
    @Override
    protected void updateData(String newData) {
        if (!newData.equals(data)) {
            data = newData;
            notifyObservers();
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this, data);
        }
    }
}
