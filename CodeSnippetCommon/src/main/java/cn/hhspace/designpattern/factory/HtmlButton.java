package cn.hhspace.designpattern.factory;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 17:51
 * @Descriptions: Html下的Button实现
 */
public class HtmlButton implements Button{

    @Override
    public void render() {
        System.out.println("<button>Test Button</button>");
        onClick();
    }

    @Override
    public void onClick() {
        System.out.println("Click! Button says - 'Hello World!'");
    }
}
