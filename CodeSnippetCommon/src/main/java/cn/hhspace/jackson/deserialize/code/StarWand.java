package cn.hhspace.jackson.deserialize.code;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/19 8:01 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */
public class StarWand implements Equipment2 {

    private String name;
    private int length;
    private int price;
    private List<String> effect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<String> getEffect() {
        return effect;
    }

    public void setEffect(List<String> effect) {
        this.effect = effect;
    }
}
