package cn.hhspace.designpattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 14:51
 * @Descriptions:
 */
public abstract class DataSource {
    protected String data = "";

    protected List<Observer> observers = new ArrayList<>();

    public String getData() {
        return data;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    abstract protected void updateData(String newData);

    abstract public void notifyObservers();
}
