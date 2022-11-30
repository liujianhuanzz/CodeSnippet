package cn.hhspace.etl.env.yarn;

import cn.hhspace.etl.config.EtlOnYarnServerCfg;
import cn.hhspace.etl.framework.JsonFileBeanFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/24 13:24
 * @Descriptions: 将flow运行在Yarn上的客户端
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class EtlOnYarnClient {

    private static final Logger logger = LoggerFactory.getLogger(EtlOnYarnClient.class);
    /**
     * Yarn配置
     */
    private Configuration conf;
    /**
     * Yarn客户端
     */
    private YarnClient yarnClient;

    /**
     * app master class
     */
    private String appMasterMainClass;
    /**
     * Command line options
     */
    private Options opts;
    private String appName = "";
    // App master priority
    private int amPriority = 0;
    // Queue for App master
    private String amQueue = "";
    // Amt. of memory resource to request for to run the App Master
    private int amMemory = 10;
    // Amt. of virtual core resource to request for to run the App Master
    private int amVCores = 1;

    // Application master jar file
    private String appMasterJar = "";

    // Shell command to be executed
    private String shellCommand = "";
    // Location of shell script
    private String shellScriptPath = "";
    // Args to be passed to the shell command
    private String[] shellArgs = new String[] {};
    // Env variables to be setup for the shell command
    private Map<String, String> shellEnv = new HashMap<String, String>();
    // Shell Command Container priority
    private int shellCmdPriority = 0;

    // Amt of memory to request for container in which shell script will be executed
    private int containerMemory = 10;
    // Amt. of virtual cores to request for container in which shell script will be executed
    private int containerVirtualCores = 1;
    // No. of containers in which the shell script needs to be executed
    private int numContainers = 1;
    private String nodeLabelExpression = null;

    // log4j.properties file
    // if available, add to local resources and set into classpath
    private String log4jPropFile = "";

    // Start time for client
    private final long clientStartTime = System.currentTimeMillis();
    // Timeout threshold for client. Kill app after time interval expires.
    private long clientTimeout = 600000;

    // flag to indicate whether to keep containers across application attempts.
    private boolean keepContainers = false;

    private long attemptFailuresValidityInterval = -1;

    // Debug flag
    boolean debugFlag = false;

    // Timeline domain ID
    private String domainId = null;

    // Flag to indicate whether to create the domain of the given ID
    private boolean toCreateDomain = false;

    // Timeline domain reader access control
    private String viewACLs = null;

    // Timeline domain writer access control
    private String modifyACLs = null;

    private static final String shellCommandPath = "shellCommands";
    private static final String shellArgsPath = "shellArgs";
    // Hardcoded path to custom log_properties
    private static final String log4jPath = "log4j.properties";

    public static final String SCRIPT_PATH = "ExecScript";
    public static final String JAR_PATH = "AppMaster.jar";

    public static final String DEPLOY_FILE_PATH = "ExecDeploy.json";

    private String deployId;

    private String deployConfigJson;

    private String runInstanceId;

    public static void main(String[] args) {
        boolean result = false;
        try {
            EtlOnYarnClient client = new EtlOnYarnClient();
            logger.info("初始化客户端");
            try {
                boolean doRun = client.init(args);
                if (!doRun) {
                    System.exit(0);
                }
            } catch (IllegalArgumentException e) {
                logger.error("参数传入有误", e);
                client.printUsage();
                System.exit(-1);
            }
            result = client.run();
        } catch (Throwable e) {
            logger.error("运行客户端时出错", e);
            System.exit(1);
        }

        if (result) {
            logger.info("任务成功运行完成");
            System.exit(0);
        }
        logger.error("任务运行失败");
        System.exit(2);
    }

    public EtlOnYarnClient(Configuration conf) {
        this("cn.hhspace.etl.env.yarn.EtlApplicationMaster", conf);
    }

    public EtlOnYarnClient (String appMasterMainClass, Configuration conf) {
        this.conf = conf;
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        this.appMasterMainClass = appMasterMainClass;
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(conf);
        opts = new Options();
        opts.addOption(EtlOnYarnContants.DEPLOY_ID_STRING, true, "Flow部署的ID");
        opts.addOption(EtlOnYarnContants.RUN_INSTANCE_ID_STRING, true, "运行实例ID");
        opts.addOption(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING, true, "Flow部署的JSON配置文件");
    }

    public  EtlOnYarnClient() {
        this(new Configuration());
    }

    /**
     * 打印如何使用
     */
    private void printUsage() {
        new HelpFormatter().printHelp("EtlOnYarnClient", opts);
    }

    /**
     * 解析命令行参数，初始化BeanFactory
     * @param args
     * @return
     * @throws ParseException
     */
    public boolean init(String[] args) throws Exception {
        CommandLine cliParser = new GnuParser().parse(opts, args);
        if (0 ==  args.length) {
            throw new IllegalArgumentException("No args specified for client to initialize");
        }

        if (cliParser.hasOption("help")) {
            printUsage();
            return false;
        }

        if (!cliParser.hasOption(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING) ||
                !cliParser.hasOption(EtlOnYarnContants.DEPLOY_ID_STRING) ||
                !cliParser.hasOption(EtlOnYarnContants.RUN_INSTANCE_ID_STRING)) {
            printUsage();
            return false;
        }

        deployId = cliParser.getOptionValue(EtlOnYarnContants.DEPLOY_ID_STRING);
        deployConfigJson = cliParser.getOptionValue(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING);
        runInstanceId = cliParser.getOptionValue(EtlOnYarnContants.RUN_INSTANCE_ID_STRING);

        //初始化BeanFactory
        JsonFileBeanFactory beanFactory = new JsonFileBeanFactory();
        beanFactory.setJsonConfigFileName(cliParser.getOptionValue(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING));
        beanFactory.init(null);

        List<String> etlOnYarnServerCfgs = beanFactory.getBeanIdsForType(EtlOnYarnContants.ETL_ON_YARN_SERVER_CONFIG_STRING);
        if (etlOnYarnServerCfgs.size() > 1 || etlOnYarnServerCfgs.size() == 0) {
            throw new Exception("同一次部署中不能有两个ETLOnYarnServer的配置");
        }

        EtlOnYarnServerCfg etlOnYarnServerCfg = beanFactory.getBean(etlOnYarnServerCfgs.get(0), EtlOnYarnServerCfg.class);
        Map<String, String> paramMap = etlOnYarnServerCfg.getParamMap();

        initParams(paramMap);

        return true;
    }

    private void initParams(Map<String, String> paramMap) {
        if (null != paramMap.get("app_name")) {
            appName = paramMap.get("app_name");
        }

        if (null != paramMap.get("am_priority")) {
            amPriority = Integer.parseInt(paramMap.get("am_priority"));
        }

        if (null != paramMap.get("am_queue")) {
            amQueue = paramMap.get("am_queue");
        }

        if (null != paramMap.get("am_vcores")) {
            amVCores = Integer.parseInt(paramMap.get("am_vcores"));
        }

        if (null != paramMap.get("am_memory")) {
            amMemory = Integer.parseInt(paramMap.get("am_memory"));
        }

        if (null != paramMap.get("app_master_jar")) {
            appMasterJar = paramMap.get("app_master_jar");
        }

        if (null != paramMap.get("shell_command")) {
            shellCommand = paramMap.get("shell_command");
        }

        if (null != paramMap.get("shell_script_path")) {
            shellScriptPath = paramMap.get("shell_script_path");
        }

        if (null != paramMap.get("container_memory")) {
            containerMemory = Integer.parseInt(paramMap.get("container_memory"));
        }

        if (null != paramMap.get("container_virtual_cores")) {
            containerVirtualCores = Integer.parseInt(paramMap.get("container_virtual_cores"));
        }

        if (null != paramMap.get("num_containers")) {
            numContainers = Integer.parseInt(paramMap.get("num_containers"));
        }
    }

    /**
     * 客户端运行的主要逻辑
     * @return
     */
    public boolean run() throws IOException, YarnException {
        logger.info("开始运行EtlOnYarn客户端");
        yarnClient.start();

        /**
         * 打印集群、队列等当前状态信息
         */

        YarnClusterMetrics clusterMetrics = yarnClient.getYarnClusterMetrics();
        logger.info("Got Cluster metric info from ASM"
                + ", numNodeManagers=" + clusterMetrics.getNumNodeManagers());

        List<NodeReport> clusterNodeReports = yarnClient.getNodeReports(
                NodeState.RUNNING);
        logger.info("Got Cluster node info from ASM");
        for (NodeReport node : clusterNodeReports) {
            logger.info("Got node report from ASM for"
                    + ", nodeId=" + node.getNodeId()
                    + ", nodeAddress" + node.getHttpAddress()
                    + ", nodeRackName" + node.getRackName()
                    + ", nodeNumContainers" + node.getNumContainers());
        }

        QueueInfo queueInfo = yarnClient.getQueueInfo("default");
        logger.info("Queue info"
                + ", queueName=" + queueInfo.getQueueName()
                + ", queueCurrentCapacity=" + queueInfo.getCurrentCapacity()
                + ", queueMaxCapacity=" + queueInfo.getMaximumCapacity()
                + ", queueApplicationCount=" + queueInfo.getApplications().size()
                + ", queueChildQueueCount=" + queueInfo.getChildQueues().size());

        List<QueueUserACLInfo> listAclInfo = yarnClient.getQueueAclsInfo();
        for (QueueUserACLInfo aclInfo : listAclInfo) {
            for (QueueACL userAcl : aclInfo.getUserAcls()) {
                logger.info("User ACL Info for Queue"
                        + ", queueName=" + aclInfo.getQueueName()
                        + ", userAcl=" + userAcl.name());
            }
        }

        /**
         * 获取一个新的application
         */
        YarnClientApplication app = yarnClient.createApplication();
        GetNewApplicationResponse appResponse = app.getNewApplicationResponse();

        //TODO 根据配置校验一下

        ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
        ApplicationId appId = appContext.getApplicationId();

        appContext.setKeepContainersAcrossApplicationAttempts(keepContainers);
        appContext.setApplicationName(appName);
        appContext.setApplicationType("ETL");

        if (attemptFailuresValidityInterval >= 0) {
            appContext.setAttemptFailuresValidityInterval(attemptFailuresValidityInterval);
        }

        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

        logger.info("Copy App Master jar from local filesystem and add to local environment");
        // Copy the application master jar to the filesystem
        // Create a local resource to point to the destination jar path
        FileSystem fs = FileSystem.get(conf);

        //设置jar包的信息

        List<String> jarResult = addToLocalResources(fs, appMasterJar, JAR_PATH, appId.toString(),
                localResources, null);
        String hdfsJarLocation = jarResult.get(0);
        String hdfsJarLen = jarResult.get(1);
        String hdfsJarTimestamp = jarResult.get(2);

        //设置deploy file的信息

        List<String> deployResult = addToLocalResources(fs, deployConfigJson, DEPLOY_FILE_PATH, appId.toString(),
                localResources, null);
        String hdfsDeployFileLocation = deployResult.get(0);
        String hdfsDeployFileLen = deployResult.get(1);
        String hdfsDeployFileTimestamp = deployResult.get(2);

        /*;

        */

        String hdfsShellScriptLocation = "";
        long hdfsShellScriptLen = 0;
        long hdfsShellScriptTimestamp = 0;
        if (!shellScriptPath.isEmpty()) {
            Path shellSrc = new Path(shellScriptPath);
            String shellPathSuffix =
                    appName + "/" + appId.toString() + "/" + SCRIPT_PATH;
            Path shellDst =
                    new Path(fs.getHomeDirectory(), shellPathSuffix);
            fs.copyFromLocalFile(false, true, shellSrc, shellDst);
            hdfsShellScriptLocation = shellDst.toUri().toString();
            FileStatus shellFileStatus = fs.getFileStatus(shellDst);
            hdfsShellScriptLen = shellFileStatus.getLen();
            hdfsShellScriptTimestamp = shellFileStatus.getModificationTime();
        }

        if (!shellCommand.isEmpty()) {
            addToLocalResources(fs, null, shellCommandPath, appId.toString(),
                    localResources, shellCommand);
        }

        if (shellArgs.length > 0) {
            addToLocalResources(fs, null, shellArgsPath, appId.toString(),
                    localResources, StringUtils.join(shellArgs, " "));
        }

        logger.info("Set the environment for the application master");
        Map<String, String> env = new HashMap<String, String>();

        // put location of shell script / jar file / deploy file into env
        // using the env info, the application master will create the correct local resource for the
        // eventual containers that will be launched to execute the shell scripts
        env.put(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LOCATION, hdfsJarLocation);
        env.put(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LEN, hdfsJarLen);
        env.put(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_TIMESTAMP, hdfsJarTimestamp);

        env.put(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LOCATION, hdfsDeployFileLocation);
        env.put(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LEN, hdfsDeployFileLen);
        env.put(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_TIMESTAMP, hdfsDeployFileTimestamp);

        env.put(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LOCATION, hdfsShellScriptLocation);
        env.put(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_TIMESTAMP, Long.toString(hdfsShellScriptTimestamp));
        env.put(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LEN, Long.toString(hdfsShellScriptLen));

        env.put(EtlOnYarnContants.DEPLOY_ID_STRING, deployId);
        env.put(EtlOnYarnContants.RUN_INSTANCE_ID_STRING, runInstanceId);
        env.put(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING, deployConfigJson);

        StringBuilder classPathEnv = new StringBuilder(ApplicationConstants.Environment.CLASSPATH.$$())
                .append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
        for (String c : conf.getStrings(
                YarnConfiguration.YARN_APPLICATION_CLASSPATH,
                YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
            classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
            classPathEnv.append(c.trim());
        }
        classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR).append(
                "./log4j.properties");
        classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./" + JAR_PATH);

        // add the runtime classpath needed for tests to work
        if (conf.getBoolean(YarnConfiguration.IS_MINI_YARN_CLUSTER, false)) {
            classPathEnv.append(':');
            classPathEnv.append(System.getProperty("java.class.path"));
        }

        env.put("CLASSPATH", classPathEnv.toString());

        // Set the necessary command to execute the application master
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);

        // Set java executable command
        logger.info("Setting up app master command");
        vargs.add(ApplicationConstants.Environment.JAVA_HOME.$$() + "/bin/java");
        // Set Xmx based on am memory size
        vargs.add("-Xmx" + amMemory + "m");
        // Set class name
        vargs.add(appMasterMainClass);
        // Set params for Application Master
        vargs.add("--container_memory " + String.valueOf(containerMemory));
        vargs.add("--container_vcores " + String.valueOf(containerVirtualCores));
        vargs.add("--num_containers " + String.valueOf(numContainers));
        if (null != nodeLabelExpression) {
            appContext.setNodeLabelExpression(nodeLabelExpression);
        }
        vargs.add("--priority " + String.valueOf(shellCmdPriority));

        for (Map.Entry<String, String> entry : shellEnv.entrySet()) {
            vargs.add("--shell_env " + entry.getKey() + "=" + entry.getValue());
        }
        if (debugFlag) {
            vargs.add("--debug");
        }

        vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stdout");
        vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stderr");

        // Get final commmand
        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }

        logger.info("Completed setting up app master command " + command.toString());
        List<String> commands = new ArrayList<String>();
        commands.add(command.toString());

        // 设置AM启动的container的上下文
        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(
                localResources, env, commands, null, null, null);

        //设置所需要的资源
        Resource capability = Resource.newInstance(amMemory, amVCores);
        appContext.setResource(capability);

        // Service data is a binary blob that can be passed to the application
        // Not needed in this scenario
        // amContainer.setServiceData(serviceData);

        // Setup security tokens
        if (UserGroupInformation.isSecurityEnabled()) {
            // Note: Credentials class is marked as LimitedPrivate for HDFS and MapReduce
            Credentials credentials = new Credentials();
            String tokenRenewer = conf.get(YarnConfiguration.RM_PRINCIPAL);
            if (tokenRenewer == null || tokenRenewer.length() == 0) {
                throw new IOException(
                        "Can't get Master Kerberos principal for the RM to use as renewer");
            }

            // For now, only getting tokens for the default file-system.
            final Token<?> tokens[] =
                    fs.addDelegationTokens(tokenRenewer, credentials);
            if (tokens != null) {
                for (Token<?> token : tokens) {
                    logger.info("Got dt for " + fs.getUri() + "; " + token);
                }
            }
            DataOutputBuffer dob = new DataOutputBuffer();
            credentials.writeTokenStorageToStream(dob);
            ByteBuffer fsTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
            amContainer.setTokens(fsTokens);
        }

        appContext.setAMContainerSpec(amContainer);

        // Set the priority for the application master
        // TODO - what is the range for priority? how to decide?
        Priority pri = Priority.newInstance(amPriority);
        appContext.setPriority(pri);

        // Set the queue to which this application is to be submitted in the RM
        appContext.setQueue(amQueue);

        // Submit the application to the applications manager
        // SubmitApplicationResponse submitResp = applicationsManager.submitApplication(appRequest);
        // Ignore the response as either a valid response object is returned on success
        // or an exception thrown to denote some form of a failure
        logger.info("Submitting application to ASM");

        yarnClient.submitApplication(appContext);

        // TODO
        // Try submitting the same request again
        // app submission failure?

        // Monitor the application
        return monitorApplication(appId);
    }

    private boolean monitorApplication(ApplicationId appId)
            throws YarnException, IOException {

        while (true) {

            // Check app status every 1 second.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug("Thread sleep in monitoring loop interrupted");
            }

            // Get application report for the appId we are interested in
            ApplicationReport report = yarnClient.getApplicationReport(appId);

            logger.info("Got application report from ASM for"
                    + ", appId=" + appId.getId()
                    + ", clientToAMToken=" + report.getClientToAMToken()
                    + ", appDiagnostics=" + report.getDiagnostics()
                    + ", appMasterHost=" + report.getHost()
                    + ", appQueue=" + report.getQueue()
                    + ", appMasterRpcPort=" + report.getRpcPort()
                    + ", appStartTime=" + report.getStartTime()
                    + ", yarnAppState=" + report.getYarnApplicationState().toString()
                    + ", distributedFinalState=" + report.getFinalApplicationStatus().toString()
                    + ", appTrackingUrl=" + report.getTrackingUrl()
                    + ", appUser=" + report.getUser());

            YarnApplicationState state = report.getYarnApplicationState();
            FinalApplicationStatus dsStatus = report.getFinalApplicationStatus();
            if (YarnApplicationState.FINISHED == state) {
                if (FinalApplicationStatus.SUCCEEDED == dsStatus) {
                    logger.info("Application has completed successfully. Breaking monitoring loop");
                    return true;
                } else {
                    logger.info("Application did finished unsuccessfully."
                            + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                            + ". Breaking monitoring loop");
                    return false;
                }
            } else if (YarnApplicationState.KILLED == state
                    || YarnApplicationState.FAILED == state) {
                logger.info("Application did not finish."
                        + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                        + ". Breaking monitoring loop");
                return false;
            }

            if (System.currentTimeMillis() > (clientStartTime + clientTimeout)) {
                logger.info("Reached client specified timeout for application. Killing application");
                forceKillApplication(appId);
                return false;
            }
        }
    }

    /**
     * 杀掉一个已经提交的任务
     * @param appId
     * @throws YarnException
     * @throws IOException
     */
    private void forceKillApplication(ApplicationId appId)
            throws YarnException, IOException {
        yarnClient.killApplication(appId);
    }

    private List<String> addToLocalResources(FileSystem fs, String fileSrcPath,
                                     String fileDstPath, String appId, Map<String, LocalResource> localResources,
                                     String resources) throws IOException {
        String suffix =
                appName + "/" + appId + "/" + fileDstPath;
        Path dst =
                new Path(fs.getHomeDirectory(), suffix);
        if (fileSrcPath == null) {
            FSDataOutputStream ostream = null;
            try {
                ostream = FileSystem
                        .create(fs, dst, new FsPermission((short) 0710));
                ostream.writeUTF(resources);
            } finally {
                IOUtils.closeQuietly(ostream);
            }
        } else {
            fs.copyFromLocalFile(new Path(fileSrcPath), dst);
        }
        FileStatus scFileStatus = fs.getFileStatus(dst);
        LocalResource scRsrc =
                LocalResource.newInstance(
                        ConverterUtils.getYarnUrlFromURI(dst.toUri()),
                        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
                        scFileStatus.getLen(), scFileStatus.getModificationTime());
        localResources.put(fileDstPath, scRsrc);

        List<String> result = new ArrayList<String>();
        result.add(dst.toUri().toString());
        result.add(Long.toString(scFileStatus.getLen()));
        result.add(Long.toString(scFileStatus.getModificationTime()));
        return result;
    }
}
