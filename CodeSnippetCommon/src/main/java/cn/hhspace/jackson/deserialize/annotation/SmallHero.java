package cn.hhspace.jackson.deserialize.annotation;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/20 15:40
 * @Descriptions:
 */
public class SmallHero implements Hero{
    private String name;

    private int size;

    private Equipment1 equipment;

    private String height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Equipment1 getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment1 equipment) {
        this.equipment = equipment;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
