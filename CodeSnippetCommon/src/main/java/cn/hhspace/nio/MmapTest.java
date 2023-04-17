package cn.hhspace.nio;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MmapTest {
    public static void main(String[] args) throws IOException {
        String filePath = "large_file";
        int fileSize = 1024 * 1024 * 1024;

        // Create a large file
        byte[] data = new byte[fileSize];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }

        // Read file with normal IO
        long start = System.currentTimeMillis();
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024 * 1024];
            while (is.read(buffer) != -1) {}
        }
        long end = System.currentTimeMillis();
        long ioTime = end - start;
        System.out.println("IO read time: " + ioTime + " ms");

        // Read file with Mmap
        start = System.currentTimeMillis();
        try (FileChannel fc = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
            MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            while (buffer.hasRemaining()) {
                buffer.get();
            }
        }
        end = System.currentTimeMillis();
        long mmapTime = end - start;
        System.out.println("Mmap read time: " + mmapTime + " ms");
    }
}

