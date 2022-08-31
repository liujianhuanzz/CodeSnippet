package cn.hhspace.designpattern.factory;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 17:59
 * @Descriptions:
 */
public abstract class Dialog {
    public void renderWindow() {
        Button okButton = createButton();
        okButton.render();
    }

    public abstract Button createButton();
}
