package cn.hhspace.guava;


import java.util.function.Supplier;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/23 18:12
 * @Descriptions:
 */
public class SupplierTest {
    public static void main(String[] args) {
        Supplier<String> resultSupplier = () -> {
          return sayHello();
        };

        System.out.println("Supplier : " + resultSupplier.get());
    }

    private static String sayHello() {
        return "Hello Supplier";
    }

}
