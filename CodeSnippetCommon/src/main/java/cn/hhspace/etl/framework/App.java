package cn.hhspace.etl.framework;

import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/15 18:06
 * @Descriptions: standalone应用程序的基类，新的应用程序只需要继承这个类
 */
public abstract class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    /**
     * 应用程序名称
     */
    private String appName = "APP";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 应用程序目录
     */
    private String appDir;

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    /**
     * 应用程序配置目录
     */
    private String configDir;

    public String getConfigDir() {
        if (null != configDir) {
            return configDir;
        } else {
            return appDir + "/conf";
        }
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    /**
     * 应用程序日志目录
     */
    private String logDir;

    public String getLogDir() {
        if (null != logDir) {
            return logDir;
        } else {
            return appDir + "/log";
        }
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    protected String deployId;

    protected String runInstanceId;

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getRunInstanceId() {
        return runInstanceId;
    }

    public void setRunInstanceId(String runInstanceId) {
        this.runInstanceId = runInstanceId;
    }

    /**
     * 应用使用的插件工厂
     */
    @Autowired
    protected BeanFactory beanFactory = null;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private Thread shutdownHook = null;
    private boolean active = false;
    private boolean shutdownFlag = false;
    final Object startupShutdownMonitor=new Object();

    protected void registerShutdownHook() {
        if (null == this.shutdownHook) {
            this.shutdownHook = new Thread("shutdownHook") {
                @Override
                public void run() {
                    doClose();
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    /**
     * 显式的手动调用关闭
     */
    private void close() {
        synchronized (this.startupShutdownMonitor) {
            doClose();

            if (null != this.shutdownHook) {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
        }
    }

    protected void doClose()  {

        if (logger.isInfoEnabled()) {
            logger.info("应用关闭:" + this.getAppName());
        }
        try {
            beanFactory.stop();
            beanFactory.destroy();
        } catch (Exception e) {
            logger.warn("Error occured when close beanfactory.",e);
        }
        this.active = false;
        this.shutdownFlag=true;
    }

    private static App _instance = null;

    public static App getInstance() {
        return _instance;
    }

    /**
     * 应用初始化
     */
    public void init() throws Exception{
        _instance = this;

        //获取并设置配置目录
        String confDir = System.getProperty("conf.dir");
        if (!Strings.isNullOrEmpty(confDir)) {
            setConfigDir(confDir);
        }

        //获取并设置日志目录
        String logDir = System.getProperty("log.dir");
        if (!Strings.isNullOrEmpty(logDir)) {
            setLogDir(logDir);
        }

        //获取并设置应用根目录
        String baseDir = System.getProperty("base.dir");
        if (!Strings.isNullOrEmpty(baseDir)) {
            setAppDir(baseDir);
        }

        synchronized (startupShutdownMonitor) {
            beanFactory.init(null);
        }
    }

    /**
     * 应用启动
     */
    public void start() throws Exception {
        synchronized (startupShutdownMonitor) {
            beanFactory.start();
            this.active = true;
        }
    }

    /**
     * 应用销毁
     */
    public void destroy() {
        close();
    }
}
