package cn.hhspace.designpattern.builder;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 12:07
 * @Descriptions: 测试Computer的建造者模式
 */
public class TestBuildComputer {
    public static void main(String[] args) {
        Computer computer = new Computer.Builder("Intel", "Sam")
                .setDisplay("Sam 24")
                .setKeyBoard("LuoJi")
                .setUsbCount(2)
                .build();

        System.out.println(computer.toString());
    }
}
