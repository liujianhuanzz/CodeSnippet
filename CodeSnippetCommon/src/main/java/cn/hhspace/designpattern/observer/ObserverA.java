package cn.hhspace.designpattern.observer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 15:27
 * @Descriptions:
 */
public class ObserverA implements Observer{
    private View view;

    public ObserverA(View view) {
        this.view = view;
    }

    @Override
    public void update(DataSource ds, String data) {
        System.out.println("观察到" + ds.getClass().getSimpleName() + "发生变化，更新视图");
        view.show(data);
    }
}
