package cn.hhspace.rpcdemo.entity;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/14 1:36 下午
 * @Package: cn.hhspace.rpcdemo.entity
 */
public class InfoUser {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String address;

    public InfoUser(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public InfoUser(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
