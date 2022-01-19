package cn.hhspace.jackson.deserialize.code;

import java.util.List;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/19 8:02 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */
public class Daedalus implements Equipment2 {

    private String name;
    private String weight;
    private int damage;
    private List<String> roles;
    private Map<String,String> origin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, String> getOrigin() {
        return origin;
    }

    public void setOrigin(Map<String, String> origin) {
        this.origin = origin;
    }
}
