package cn.hhspace.jackson.deserialize.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/19 8:06 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */
public class TestDeserialize {
    public static void main(String[] args) throws IOException {
        String starWandStr = "{\n" +
                "    \"name\":\"Star wand\" ,\n" +
                "    \"length\":35,\n" +
                "    \"price\":120,\n" +
                "    \"effect\":[\"getting greater\", \"getting handsome\",\"getting rich\"]\n" +
                "}";

        String daedalusStr = "{\n" +
                "    \"name\":\"Daedalus\",\n" +
                "    \"weight\":\"5kg\",\n" +
                "    \"damage\":1200,\n" +
                "    \"roles\":[\"assassinator\",\"soldier\"],\n" +
                "    \"origin\":{\n" +
                "             \"name\":\"Mainland of warcraft\",\n" +
                "             \"date\":\"142-12-25\"\n" +
                "    }\n" +
                "}";

        String hugeDrinkStr = "{\n" +
                "    \"name\":\"Huge drink\",\n" +
                "    \"capacity\":500000,\n" +
                "    \"effect\":\"quenching your thirst and tasting good\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        StarWand starWand = (StarWand)mapper.readValue(starWandStr, Equipment1.class);
        Daedalus daedalus = (Daedalus)mapper.readValue(daedalusStr, Equipment1.class);
        HugeDrink hugeDrink = (HugeDrink)mapper.readValue(hugeDrinkStr, Equipment1.class);

        System.out.println("大佬！您已获得星空魔杖！属性增幅："+ starWand.getEffect().toString()+"！");
        System.out.println("大佬！您已获得代达罗斯之殇，增加了 " + daedalus.getDamage() + " 点输出！");
        System.out.println("大佬！您已获得代达巨大瓶饮料，it "+ hugeDrink.getEffect()+"!");
    }
}
