package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.app.LocalServerCmd;
import cn.hhspace.etl.app.ServerCmd;
import cn.hhspace.etl.etlserver.ProcessingOffset;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/29 16:42
 * @Descriptions:
 */
public class SocketCmdTest {

    private Map<String, Socket> socketMap = new HashMap<>();

    @Test
    public void testStatusCmd() throws Exception{
        LocalServerCmd cmd = new LocalServerCmd();
        cmd.setCmd(ServerCmd.STATUS);
        cmd.setRunInstanceId("yyyy");
        LocalServerCmd resultCmd = parseCmd(cmd, "10.48.202.68");
        assertEquals("STATUS_RUNNING", resultCmd.getResult().toString());
    }

    @Test
    public void testGetOffsetCmd() throws Exception {
        LocalServerCmd cmd = new LocalServerCmd();
        cmd.setCmd(ServerCmd.GETOFFSET);
        cmd.setRunInstanceId("yyyy");
        LocalServerCmd resultCmd = parseCmd(cmd, "10.48.202.68");
        ProcessingOffset processingOffset = JSON.toJavaObject((JSONObject)resultCmd.getResult(), ProcessingOffset.class);
        assertNotNull(processingOffset);
    }

    @Test
    public void testGetEpsCmd() throws Exception {
        while (true) {
            LocalServerCmd cmd = new LocalServerCmd();
            cmd.setCmd(ServerCmd.GETEPS);
            cmd.setRunInstanceId("yyyy");
            long totalEps = 0;
            String[] hosts = new String[]{"10.48.202.68", "10.48.202.69", "10.48.202.70"};
            for (String host : hosts) {
                LocalServerCmd resultCmd = parseCmd(cmd, host);
                long tmp = Long.parseLong(resultCmd.getResult().toString());
                System.out.println(host + " : " + tmp);
                totalEps += tmp;
            }

            System.out.println("total : " + totalEps);
            Thread.sleep(30000);
        }
    }

    public String sendCmd(LocalServerCmd cmd, String host) throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(host, 7521);
        String result;
        Socket socket;
        if (socketMap.containsKey(host)) {
            socket = socketMap.get(host);
        } else {
            socket = new Socket();
            socket.connect(socketAddress, 300000);
            socket.setSoTimeout(30000);
            socketMap.put(host, socket);
        }

        String s = JSON.toJSONString(cmd);

        try(ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            oos.writeUTF(s);
            oos.flush();

            try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
                result = ois.readUTF();
            }
        }

        return  result;
    }

    public LocalServerCmd parseCmd(LocalServerCmd cmd, String host) throws Exception {
        String result = sendCmd(cmd, host);
        LocalServerCmd resultCmd = JSON.parseObject(result, LocalServerCmd.class);

        if (!resultCmd.isValid()) {
            throw new Exception("远程调用返回值非法");
        }

        if (resultCmd.isExceptionFlag()) {
            throw new Exception("远程调用错误:" + resultCmd.getExceptionMsg());
        }
        return resultCmd;
    }

}
