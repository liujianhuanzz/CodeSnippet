package cn.hhspace.jackson.deserialize.code;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/19 8:03 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */
public class HugeDrink implements Equipment2 {

    private String name;
    private int capacity;
    private String effect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
