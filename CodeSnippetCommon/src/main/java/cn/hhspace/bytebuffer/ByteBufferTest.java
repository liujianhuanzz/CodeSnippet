package cn.hhspace.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/22 10:01
 * @Descriptions:
 */
public class ByteBufferTest {

    public static void main(String[] args) {
        String str = "abcde";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        print("--------------------allocate()----------------");
        printBufferInfo(buffer);

        print("----------------------put()------------------");
        buffer.put(str.getBytes());
        printBufferInfo(buffer);

        print("----------------------flip()------------------");
        buffer.flip();
        printBufferInfo(buffer);

        print("---------------------get()-------------------");
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        print(new String(dst, 0, dst.length));
        printBufferInfo(buffer);

        print("--------------------rewind()-----------------");
        buffer.rewind();
        printBufferInfo(buffer);

        print("--------------------clear()-----------------");
        buffer.clear();
        printBufferInfo(buffer);

    }

    private static void printBufferInfo(ByteBuffer buffer) {
        print(positionOutput(buffer.position()));
        print(limitOutput(buffer.limit()));
        print(capacityOutput(buffer.capacity()));
    }


    private static String positionOutput(int position) {
        return "Position: " + position;
    }

    private static String limitOutput(int limit) {
        return "Limit: " + limit;
    }

    private static String capacityOutput(int capacity) {
        return "Capacity: " + capacity;
    }

    private static void print(String s) {
        System.out.println(s);
    }
}
