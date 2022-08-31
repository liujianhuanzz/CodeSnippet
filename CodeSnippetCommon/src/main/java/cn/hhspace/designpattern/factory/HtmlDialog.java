package cn.hhspace.designpattern.factory;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 18:01
 * @Descriptions:
 */
public class HtmlDialog extends Dialog {

    @Override
    public Button createButton() {
        return new HtmlButton();
    }
}
