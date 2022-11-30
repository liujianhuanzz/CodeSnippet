package cn.hhspace.etl.app;

import cn.hhspace.etl.config.DeployCfg;
import cn.hhspace.etl.deploy.Deploy;
import cn.hhspace.etl.deploy.DeployContext;
import cn.hhspace.etl.etlserver.DeployServer;
import cn.hhspace.etl.framework.App;
import cn.hhspace.etl.framework.JsonFileBeanFactory;
import cn.hhspace.etl.utils.ResourceUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/15 20:45
 * @Descriptions: 一个本地运行的App实例
 */
public class LocalRunningApp extends App {
    public static final Logger logger = LoggerFactory.getLogger(LocalRunningApp.class);

    private Deploy deploy;

    private DeployCfg deployCfg;

    protected String deployFileName;

    private DeployContext context;

    private DeployServer deployServer;

    private int serverCtrlPort;

    public String getDeployFileName() {
        return deployFileName;
    }

    public void setDeployFileName(String deployFileName) {
        this.deployFileName = deployFileName;
    }

    private ServerControl serverControl;

    private class ServerControl implements Runnable {

        volatile boolean quitFlag = false;

        ServerSocket serverSocket;

        ServerControl(int port) throws IOException {
            serverSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
        }

        public void close() {
            if (!quitFlag) {
                if (null != deployServer) {
                    try {
                        deployServer.stop();
                    } catch (Exception e) {
                        logger.warn("关闭本地服务器报错", e);
                    }
                }
                quitFlag = false;
            }
        }

        public Object sendCmd(LocalServerCmd cmd) {
            switch (cmd.getCmd()) {
                case STOP:
                    deployServer.stop();
                    quitFlag = true;
                    return Boolean.TRUE;
                case STATUS:
                    return deployServer.getServerStatus().name();
                case GETOFFSET:
                    return deployServer.getOffset();
                case GETEPS:
                    return deploy.getEps();
                default:
            }
            throw new UnsupportedOperationException("未知调用");
        }

        @Override
        public void run() {
            while (!quitFlag) {
                InetAddress addr = null;
                try (Socket socket = serverSocket.accept()) {
                    addr = socket.getInetAddress();
                    try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                        String cmdStr = ois.readUTF();
                        LocalServerCmd cmd = JSON.parseObject(cmdStr, LocalServerCmd.class);
                        if (cmd.isValid()) {
                            try {
                                cmd.setResult(sendCmd(cmd));
                                cmd.setExceptionFlag(false);
                            } catch (Exception e) {
                                cmd.setExceptionFlag(true);
                                cmd.setExceptionMsg(e.getMessage());
                            }
                            String cmdResult=JSON.toJSONString(cmd);
                            try ( ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                                output.writeUTF(cmdResult);
                                output.flush();
                            }
                        }
                    }
                } catch (Throwable e) {
                    String ip= (addr == null) ? "未知" : addr.getHostAddress();
                    logger.error("本地服务器控制端口接收到错误的控制命令,发送地址是:" + ip,e);
                }
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("本地服务器关闭控制端口报错 :", e);
            }

            logger.info("本地服务器关闭");
            System.exit(0);
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        if (!beanFactory.containsBean(deployId)) {
            throw new Exception("找不到部署Id:" + deployId);
        }
        deploy = beanFactory.getBean(deployId, Deploy.class);
        deploy.init(beanFactory);

        deployCfg = beanFactory.getBean(deploy.getConfigId(), DeployCfg.class);
        serverCtrlPort = deployCfg.getControlPort();

        if (serverCtrlPort <= 0) {
            throw new Exception("服务器对应的配置ControlPort端口错误");
        }
        context = new DeployContext();
        context.setDeployFileName(null);
        context.setDeployId(deployId);
        context.setRunInstanceId(runInstanceId);
    }

    protected void startServer() throws Exception {
        try {
            serverControl = new ServerControl(serverCtrlPort);
        } catch (SocketException se){
            throw new Exception("网络错误，可能是控制端口已经被占用",se);
        }

        Thread ctlThread = new Thread(serverControl, "LocalServerControl");
        ctlThread.start();

        String deployServerClassName = deployCfg.getDeployServerClassName();
        deployServer = (DeployServer) Class.forName(deployServerClassName, true, ResourceUtil.getClassLoader()).newInstance();
        deployServer.init(context, this.getBeanFactory());
        deployServer.start();
    }

    public static void main(String[] args) throws Exception {

        if (args.length<3) {
            return;
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        LocalRunningApp localRunningApp = new LocalRunningApp();
        localRunningApp.setAppName("LocalRunningApp");
        localRunningApp.setAppDir("");
        localRunningApp.setDeployId(args[0]);
        localRunningApp.setRunInstanceId(args[1]);
        localRunningApp.setDeployFileName(args[2]);

        JsonFileBeanFactory fileBeanFactory = new JsonFileBeanFactory();
        fileBeanFactory.setJsonConfigFileName(localRunningApp.getDeployFileName());
        localRunningApp.setBeanFactory(fileBeanFactory);
        localRunningApp.init();
        localRunningApp.startServer();

        System.out.println(df.format(new Date()) + "LocalRunningApp启动");
    }
}
