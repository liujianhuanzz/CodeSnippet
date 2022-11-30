package cn.hhspace.etl.env.yarn;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/24 13:33
 * @Descriptions:
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class EtlApplicationMaster {

    private static final Logger logger = LoggerFactory.getLogger(EtlApplicationMaster.class);

    @VisibleForTesting
    @InterfaceAudience.Private
    public static enum ETLEvent {
        ETL_APP_ATTEMPT_START, ETL_APP_ATTEMPT_END, ETL_CONTAINER_START, ETL_CONTAINER_END
    }

    @VisibleForTesting
    @InterfaceAudience.Private
    public static enum ETLEntity {
        ETL_APP_ATTEMPT, ETL_CONTAINER
    }

    private Configuration conf;

    // Handle to communicate with the Resource Manager
    @SuppressWarnings("rawtypes")
    private AMRMClientAsync amRMClient;

    // In both secure and non-secure modes, this points to the job-submitter.
    @VisibleForTesting
    UserGroupInformation appSubmitterUgi;

    // Handle to communicate with the Node Manager
    private NMClientAsync nmClientAsync;
    // Listen to process the response from the Node Manager
    private NMCallbackHandler containerListener;

    // Application Attempt Id ( combination of attemptId and fail count )
    @VisibleForTesting
    protected ApplicationAttemptId appAttemptID;

    // TODO
    // For status update for clients - yet to be implemented
    // Hostname of the container
    private String appMasterHostname = "";
    // Port on which the app master listens for status updates from clients
    private int appMasterRpcPort = -1;
    // Tracking url to which app master publishes info for clients to monitor
    private String appMasterTrackingUrl = "";

    // App Master configuration
    // No. of containers to run shell command on
    @VisibleForTesting
    protected int numTotalContainers = 1;
    // Memory to request for the container on which the shell command will run
    private int containerMemory = 10;
    // VirtualCores to request for the container on which the shell command will run
    private int containerVirtualCores = 1;
    // Priority of the request
    private int requestPriority;

    // Counter for completed containers ( complete denotes successful or failed )
    private AtomicInteger numCompletedContainers = new AtomicInteger();
    // Allocated container count so that we know how many containers has the RM
    // allocated to us
    @VisibleForTesting
    protected AtomicInteger numAllocatedContainers = new AtomicInteger();
    // Count of failed containers
    private AtomicInteger numFailedContainers = new AtomicInteger();
    // Count of containers already requested from the RM
    // Needed as once requested, we should not request for containers again.
    // Only request for more if the original requirement changes.
    @VisibleForTesting
    protected AtomicInteger numRequestedContainers = new AtomicInteger();

    // Shell command to be executed
    private String shellCommand = "";
    // Args to be passed to the shell command
    private String shellArgs = "";
    // Env variables to be setup for the shell command
    private Map<String, String> shellEnv = new HashMap<String, String>();

    // Location of shell script ( obtained from info set in env )
    // Shell script path in fs
    private String scriptPath = "";
    // Timestamp needed for creating a local resource
    private long shellScriptPathTimestamp = 0;
    // File length needed for local resource
    private long shellScriptPathLen = 0;

    private String jarPath = "";
    private long jarPathLen = 0;
    private long jarPathTimestamp = 0;

    private String deployFilePath = "";
    private long deployFilePathLen = 0;
    private long deployFilePathTimestamp = 0;

    // Timeline domain ID
    private String domainId = null;

    // Hardcoded path to shell script in launch container's local env
    private static final String ExecShellStringPath = EtlOnYarnClient.SCRIPT_PATH + ".sh";
    private static final String ExecBatScripStringtPath = EtlOnYarnClient.SCRIPT_PATH
            + ".bat";

    private static final String ExecJarFileStringPath = EtlOnYarnClient.JAR_PATH;
    private static final String ExecDepoyFileStringPath = EtlOnYarnClient.DEPLOY_FILE_PATH;

    // Hardcoded path to custom log_properties
    private static final String log4jPath = "log4j.properties";

    private static final String shellCommandPath = "shellCommands";
    private static final String shellArgsPath = "shellArgs";

    private volatile boolean done;

    private ByteBuffer allTokens;

    // Launch threads
    private List<Thread> launchThreads = new ArrayList<Thread>();

    // Timeline Client
    @VisibleForTesting
    TimelineClient timelineClient;

    private final String linux_bash_command = "bash";
    private final String windows_command = "cmd /c";

    private String deployId;

    private String deployConfigJson = "deploy.json";

    private String runInstanceId;

    public static void main(String[] args) {
        boolean result = false;
        try {
            EtlApplicationMaster appMaster = new EtlApplicationMaster();
            boolean doRun = appMaster.init(args);
            if (!doRun){
                System.exit(0);
            }
            appMaster.run();
            result = appMaster.finish();
        } catch (Throwable t) {
            logger.error("Error running ApplicationMaster", t);
            LogManager.shutdown();
            ExitUtil.terminate(1, t);
        }
        if (result) {
            logger.info("Application Master completed successfully. exiting");
            System.exit(0);
        } else {
            logger.info("Application Master failed. exiting");
            System.exit(2);
        }
    }

    public EtlApplicationMaster() {
        conf = new YarnConfiguration();
    }

    private void run() throws IOException, YarnException, InterruptedException {
        logger.info("Starting ApplicationMaster");

        // Note: Credentials, Token, UserGroupInformation, DataOutputBuffer class
        // are marked as LimitedPrivate
        Credentials credentials =
                UserGroupInformation.getCurrentUser().getCredentials();
        DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        // Now remove the AM->RM token so that containers cannot access it.
        Iterator<Token<?>> iter = credentials.getAllTokens().iterator();
        logger.info("Executing with tokens:");
        while (iter.hasNext()) {
            Token<?> token = iter.next();
            logger.info(String.valueOf(token));
            if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
                iter.remove();
            }
        }
        allTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());

        // Create appSubmitterUgi and add original tokens to it
        String appSubmitterUserName =
                System.getenv(ApplicationConstants.Environment.USER.name());
        appSubmitterUgi =
                UserGroupInformation.createRemoteUser(appSubmitterUserName);
        appSubmitterUgi.addCredentials(credentials);


        AMRMClientAsync.CallbackHandler allocListener = new RMCallbackHandler();
        amRMClient = AMRMClientAsync.createAMRMClientAsync(1000, allocListener);
        amRMClient.init(conf);
        amRMClient.start();

        containerListener = createNMCallbackHandler();
        nmClientAsync = new NMClientAsyncImpl(containerListener);
        nmClientAsync.init(conf);
        nmClientAsync.start();

        startTimelineClient(conf);
        if(timelineClient != null) {
            publishApplicationAttemptEvent(timelineClient, appAttemptID.toString(),
                    ETLEvent.ETL_APP_ATTEMPT_START, domainId, appSubmitterUgi);
        }

        // Setup local RPC Server to accept status requests directly from clients
        // TODO need to setup a protocol for client to be able to communicate to
        // the RPC server
        // TODO use the rpc port info to register with the RM for the client to
        // send requests to this app master

        // Register self with ResourceManager
        // This will start heartbeating to the RM
        appMasterHostname = NetUtils.getHostname();
        RegisterApplicationMasterResponse response = amRMClient
                .registerApplicationMaster(appMasterHostname, appMasterRpcPort,
                        appMasterTrackingUrl);
        // Dump out information about cluster capability as seen by the
        // resource manager
        int maxMem = response.getMaximumResourceCapability().getMemory();
        logger.info("Max mem capabililty of resources in this cluster " + maxMem);

        int maxVCores = response.getMaximumResourceCapability().getVirtualCores();
        logger.info("Max vcores capabililty of resources in this cluster " + maxVCores);

        // A resource ask cannot exceed the max.
        if (containerMemory > maxMem) {
            logger.info("Container memory specified above max threshold of cluster."
                    + " Using max value." + ", specified=" + containerMemory + ", max="
                    + maxMem);
            containerMemory = maxMem;
        }

        if (containerVirtualCores > maxVCores) {
            logger.info("Container virtual cores specified above max threshold of cluster."
                    + " Using max value." + ", specified=" + containerVirtualCores + ", max="
                    + maxVCores);
            containerVirtualCores = maxVCores;
        }

        List<Container> previousAMRunningContainers =
                response.getContainersFromPreviousAttempts();
        logger.info(appAttemptID + " received " + previousAMRunningContainers.size()
                + " previous attempts' running containers on AM registration.");
        numAllocatedContainers.addAndGet(previousAMRunningContainers.size());

        int numTotalContainersToRequest =
                numTotalContainers - previousAMRunningContainers.size();
        // Setup ask for containers from RM
        // Send request for containers to RM
        // Until we get our fully allocated quota, we keep on polling RM for
        // containers
        // Keep looping until all the containers are launched and shell script
        // executed on them ( regardless of success/failure).
        for (int i = 0; i < numTotalContainersToRequest; ++i) {
            AMRMClient.ContainerRequest containerAsk = setupContainerAskForRM();
            amRMClient.addContainerRequest(containerAsk);
        }
        numRequestedContainers.set(numTotalContainers);
    }

    protected boolean finish() {
        // wait for completion.
        while (!done
                && (numCompletedContainers.get() != numTotalContainers)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {}
        }

        if(timelineClient != null) {
            publishApplicationAttemptEvent(timelineClient, appAttemptID.toString(),
                    ETLEvent.ETL_APP_ATTEMPT_END, domainId, appSubmitterUgi);
        }

        // Join all launched threads
        // needed for when we time out
        // and we need to release containers
        for (Thread launchThread : launchThreads) {
            try {
                launchThread.join(10000);
            } catch (InterruptedException e) {
                logger.info("Exception thrown in thread join: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // When the application completes, it should stop all running containers
        logger.info("Application completed. Stopping running containers");
        nmClientAsync.stop();

        // When the application completes, it should send a finish application
        // signal to the RM
        logger.info("Application completed. Signalling finish to RM");

        FinalApplicationStatus appStatus;
        String appMessage = null;
        boolean success = true;
        if (numFailedContainers.get() == 0 &&
                numCompletedContainers.get() == numTotalContainers) {
            appStatus = FinalApplicationStatus.SUCCEEDED;
        } else {
            appStatus = FinalApplicationStatus.FAILED;
            appMessage = "Diagnostics." + ", total=" + numTotalContainers
                    + ", completed=" + numCompletedContainers.get() + ", allocated="
                    + numAllocatedContainers.get() + ", failed="
                    + numFailedContainers.get();
            logger.info(appMessage);
            success = false;
        }
        try {
            amRMClient.unregisterApplicationMaster(appStatus, appMessage, null);
        } catch (YarnException ex) {
            logger.error("Failed to unregister application", ex);
        } catch (IOException e) {
            logger.error("Failed to unregister application", e);
        }

        amRMClient.stop();

        // Stop Timeline Client
        if(timelineClient != null) {
            timelineClient.stop();
        }

        return success;
    }

    NMCallbackHandler createNMCallbackHandler() {
        return new NMCallbackHandler(this);
    }

    void startTimelineClient(final Configuration conf)
            throws YarnException, IOException, InterruptedException {
        try {
            appSubmitterUgi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    if (conf.getBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED,
                            YarnConfiguration.DEFAULT_TIMELINE_SERVICE_ENABLED)) {
                        // Creating the Timeline Client
                        timelineClient = TimelineClient.createTimelineClient();
                        timelineClient.init(conf);
                        timelineClient.start();
                    } else {
                        timelineClient = null;
                        logger.warn("Timeline service is not enabled");
                    }
                    return null;
                }
            });
        } catch (UndeclaredThrowableException e) {
            throw new YarnException(e.getCause());
        }
    }

    private class RMCallbackHandler implements AMRMClientAsync.CallbackHandler {
        @SuppressWarnings("unchecked")
        @Override
        public void onContainersCompleted(List<ContainerStatus> completedContainers) {
            logger.info("Got response from RM for container ask, completedCnt="
                    + completedContainers.size());
            for (ContainerStatus containerStatus : completedContainers) {
                logger.info(appAttemptID + " got container status for containerID="
                        + containerStatus.getContainerId() + ", state="
                        + containerStatus.getState() + ", exitStatus="
                        + containerStatus.getExitStatus() + ", diagnostics="
                        + containerStatus.getDiagnostics());

                // non complete containers should not be here
                assert (containerStatus.getState() == ContainerState.COMPLETE);

                // increment counters for completed/failed containers
                int exitStatus = containerStatus.getExitStatus();
                if (0 != exitStatus) {
                    // container failed
                    if (ContainerExitStatus.ABORTED != exitStatus) {
                        // shell script failed
                        // counts as completed
                        numCompletedContainers.incrementAndGet();
                        numFailedContainers.incrementAndGet();
                    } else {
                        // container was killed by framework, possibly preempted
                        // we should re-try as the container was lost for some reason
                        numAllocatedContainers.decrementAndGet();
                        numRequestedContainers.decrementAndGet();
                        // we do not need to release the container as it would be done
                        // by the RM
                    }
                } else {
                    // nothing to do
                    // container completed successfully
                    numCompletedContainers.incrementAndGet();
                    logger.info("Container completed successfully." + ", containerId="
                            + containerStatus.getContainerId());
                }
                if(timelineClient != null) {
                    publishContainerEndEvent(
                            timelineClient, containerStatus, domainId, appSubmitterUgi);
                }
            }

            // ask for more containers if any failed
            int askCount = numTotalContainers - numRequestedContainers.get();
            numRequestedContainers.addAndGet(askCount);

            if (askCount > 0) {
                for (int i = 0; i < askCount; ++i) {
                    AMRMClient.ContainerRequest containerAsk = setupContainerAskForRM();
                    amRMClient.addContainerRequest(containerAsk);
                }
            }

            if (numCompletedContainers.get() == numTotalContainers) {
                done = true;
            }
        }

        @Override
        public void onContainersAllocated(List<Container> allocatedContainers) {
            logger.info("Got response from RM for container ask, allocatedCnt="
                    + allocatedContainers.size());
            numAllocatedContainers.addAndGet(allocatedContainers.size());
            for (Container allocatedContainer : allocatedContainers) {
                logger.info("Launching shell command on a new container."
                        + ", containerId=" + allocatedContainer.getId()
                        + ", containerNode=" + allocatedContainer.getNodeId().getHost()
                        + ":" + allocatedContainer.getNodeId().getPort()
                        + ", containerNodeURI=" + allocatedContainer.getNodeHttpAddress()
                        + ", containerResourceMemory"
                        + allocatedContainer.getResource().getMemory()
                        + ", containerResourceVirtualCores"
                        + allocatedContainer.getResource().getVirtualCores());
                // + ", containerToken"
                // +allocatedContainer.getContainerToken().getIdentifier().toString());

                LaunchContainerRunnable runnableLaunchContainer =
                        new LaunchContainerRunnable(allocatedContainer, containerListener);
                Thread launchThread = new Thread(runnableLaunchContainer);

                // launch and start the container on a separate thread to keep
                // the main thread unblocked
                // as all containers may not be allocated at one go.
                launchThreads.add(launchThread);
                launchThread.start();
            }
        }

        @Override
        public void onShutdownRequest() {
            done = true;
        }

        @Override
        public void onNodesUpdated(List<NodeReport> updatedNodes) {}

        @Override
        public float getProgress() {
            // set progress to deliver to RM on next heartbeat
            float progress = (float) numCompletedContainers.get()
                    / numTotalContainers;
            return progress;
        }

        @Override
        public void onError(Throwable e) {
            done = true;
            amRMClient.stop();
        }
    }

    private class LaunchContainerRunnable implements Runnable {

        // Allocated container
        Container container;

        NMCallbackHandler containerListener;

        /**
         * @param lcontainer Allocated container
         * @param containerListener Callback handler of the container
         */
        public LaunchContainerRunnable(
                Container lcontainer, NMCallbackHandler containerListener) {
            this.container = lcontainer;
            this.containerListener = containerListener;
        }

        @Override
        /**
         * Connects to CM, sets up container launch context
         * for shell command and eventually dispatches the container
         * start request to the CM.
         */
        public void run() {
            logger.info("Setting up container launch container for containerid="
                    + container.getId());

            // Set the local resources
            Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

            // The container for the eventual shell commands needs its own local
            // resources too.
            // In this scenario, if a shell script is specified, we need to have it
            // copied and made available to the container.
            if (!scriptPath.isEmpty()) {
                Path renamedScriptPath = null;
                if (Shell.WINDOWS) {
                    renamedScriptPath = new Path(scriptPath + ".bat");
                } else {
                    renamedScriptPath = new Path(scriptPath + ".sh");
                }

                try {
                    // rename the script file based on the underlying OS syntax.
                    renameScriptFile(renamedScriptPath);
                } catch (Exception e) {
                    logger.error(
                            "Not able to add suffix (.bat/.sh) to the shell script filename",
                            e);
                    // We know we cannot continue launching the container
                    // so we should release it.
                    numCompletedContainers.incrementAndGet();
                    numFailedContainers.incrementAndGet();
                    return;
                }

                URL yarnUrl = null;
                try {
                    yarnUrl = ConverterUtils.getYarnUrlFromURI(
                            new URI(renamedScriptPath.toString()));
                } catch (URISyntaxException e) {
                    logger.error("Error when trying to use shell script path specified"
                            + " in env, path=" + renamedScriptPath, e);
                    // A failure scenario on bad input such as invalid shell script path
                    // We know we cannot continue launching the container
                    // so we should release it.
                    // TODO
                    numCompletedContainers.incrementAndGet();
                    numFailedContainers.incrementAndGet();
                    return;
                }
                LocalResource shellRsrc = LocalResource.newInstance(yarnUrl,
                        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
                        shellScriptPathLen, shellScriptPathTimestamp);
                localResources.put(Shell.WINDOWS ? ExecBatScripStringtPath :
                        ExecShellStringPath, shellRsrc);
                shellCommand = Shell.WINDOWS ? windows_command : linux_bash_command;
            }

            if (!jarPath.isEmpty()) {
                URL yarnUrl = null;
                try {
                    yarnUrl = ConverterUtils.getYarnUrlFromURI(new URI(jarPath.toString()));
                } catch (URISyntaxException e) {
                    logger.error("Error when trying to use jar file path specified"
                            + " in env, path=" + jarPath, e);
                    // A failure scenario on bad input such as invalid shell script path
                    // We know we cannot continue launching the container
                    // so we should release it.
                    // TODO
                    numCompletedContainers.incrementAndGet();
                    numFailedContainers.incrementAndGet();
                    return;
                }

                LocalResource jarFileRes = LocalResource.newInstance(yarnUrl,
                        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
                        jarPathLen, jarPathTimestamp);
                localResources.put(ExecJarFileStringPath, jarFileRes);
            }

            if (!deployFilePath.isEmpty()) {
                URL yarnUrl = null;
                try {
                    yarnUrl = ConverterUtils.getYarnUrlFromURI(new URI(deployFilePath.toString()));
                } catch (URISyntaxException e) {
                    logger.error("Error when trying to use deploy file path specified"
                            + " in env, path=" + deployFilePath, e);
                    // A failure scenario on bad input such as invalid shell script path
                    // We know we cannot continue launching the container
                    // so we should release it.
                    // TODO
                    numCompletedContainers.incrementAndGet();
                    numFailedContainers.incrementAndGet();
                    return;
                }

                LocalResource deployFileRes = LocalResource.newInstance(yarnUrl,
                        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
                        deployFilePathLen, deployFilePathTimestamp);

                localResources.put(ExecDepoyFileStringPath, deployFileRes);
            }

            // Set the necessary command to execute on the allocated container
            Vector<CharSequence> vargs = new Vector<CharSequence>(5);

            // Set executable command
            vargs.add(ApplicationConstants.Environment.JAVA_HOME.$$() + "/bin/java");

            vargs.add("-cp");
            vargs.add(ExecJarFileStringPath);
            vargs.add(shellCommand);

            vargs.add(deployId);
            vargs.add(runInstanceId);
            vargs.add(ExecDepoyFileStringPath);

            // Set shell script path
            if (!scriptPath.isEmpty()) {
                vargs.add(Shell.WINDOWS ? ExecBatScripStringtPath
                        : ExecShellStringPath);
            }

            // Set args for the shell command if any
            vargs.add(shellArgs);
            // Add log redirect params
            vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
            vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");

            // Get final commmand
            StringBuilder command = new StringBuilder();
            for (CharSequence str : vargs) {
                command.append(str).append(" ");
            }

            List<String> commands = new ArrayList<String>();
            commands.add(command.toString());

            logger.info("xxxxxxxxx" + command.toString());

            // Set up ContainerLaunchContext, setting local resource, environment,
            // command and token for constructor.

            // Note for tokens: Set up tokens for the container too. Today, for normal
            // shell commands, the container in distribute-shell doesn't need any
            // tokens. We are populating them mainly for NodeManagers to be able to
            // download anyfiles in the distributed file-system. The tokens are
            // otherwise also useful in cases, for e.g., when one is running a
            // "hadoop dfs" command inside the distributed shell.
            ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(
                    localResources, shellEnv, commands, null, allTokens.duplicate(), null);
            containerListener.addContainer(container.getId(), container);
            nmClientAsync.startContainerAsync(container, ctx);
        }
    }

    private static void publishContainerEndEvent(
            final TimelineClient timelineClient, ContainerStatus container,
            String domainId, UserGroupInformation ugi) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityId(container.getContainerId().toString());
        entity.setEntityType(ETLEntity.ETL_CONTAINER.toString());
        entity.setDomainId(domainId);
        entity.addPrimaryFilter("user", ugi.getShortUserName());
        TimelineEvent event = new TimelineEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setEventType(ETLEvent.ETL_CONTAINER_END.toString());
        event.addEventInfo("State", container.getState().name());
        event.addEventInfo("Exit Status", container.getExitStatus());
        entity.addEvent(event);
        try {
            timelineClient.putEntities(entity);
        } catch (YarnException | IOException e) {
            logger.error("Container end event could not be published for "
                    + container.getContainerId().toString(), e);
        }
    }

    private static void publishApplicationAttemptEvent(
            final TimelineClient timelineClient, String appAttemptId,
            ETLEvent appEvent, String domainId, UserGroupInformation ugi) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityId(appAttemptId);
        entity.setEntityType(ETLEntity.ETL_APP_ATTEMPT.toString());
        entity.setDomainId(domainId);
        entity.addPrimaryFilter("user", ugi.getShortUserName());
        TimelineEvent event = new TimelineEvent();
        event.setEventType(appEvent.toString());
        event.setTimestamp(System.currentTimeMillis());
        entity.addEvent(event);
        try {
            timelineClient.putEntities(entity);
        } catch (YarnException | IOException e) {
            logger.error("App Attempt "
                    + (appEvent.equals(ETLEvent.ETL_APP_ATTEMPT_START) ? "start" : "end")
                    + " event could not be published for "
                    + appAttemptId.toString(), e);
        }
    }

    private AMRMClient.ContainerRequest setupContainerAskForRM() {
        // setup requirements for hosts
        // using * as any host will do for the distributed shell app
        // set the priority for the request
        // TODO - what is the range for priority? how to decide?
        Priority pri = Priority.newInstance(requestPriority);

        // Set up resource type requirements
        // For now, memory and CPU are supported so we set memory and cpu requirements
        Resource capability = Resource.newInstance(containerMemory,
                containerVirtualCores);

        AMRMClient.ContainerRequest request = new AMRMClient.ContainerRequest(capability, null, null,
                pri);
        logger.info("Requested container ask: " + request.toString());
        return request;
    }

    public boolean init(String[] args) throws ParseException, IOException {
        Options opts = new Options();
        opts.addOption("app_attempt_id", true,
                "App Attempt ID. Not to be used unless for testing purposes");
        opts.addOption("shell_env", true,
                "Environment for shell script. Specified as env_key=env_val pairs");
        opts.addOption("container_memory", true,
                "Amount of memory in MB to be requested to run the shell command");
        opts.addOption("container_vcores", true,
                "Amount of virtual cores to be requested to run the shell command");
        opts.addOption("num_containers", true,
                "No. of containers on which the shell command needs to be executed");
        opts.addOption("priority", true, "Application Priority. Default 0");
        opts.addOption("debug", false, "Dump out debug information");

        opts.addOption("help", false, "Print usage");
        CommandLine cliParser = new GnuParser().parse(opts, args);

        if (args.length == 0) {
            printUsage(opts);
            throw new IllegalArgumentException(
                    "No args specified for application master to initialize");
        }

        //Check whether customer log4j.properties file exists
        if (fileExist(log4jPath)) {
            try {
                Log4jPropertyHelper.updateLog4jConfiguration(EtlApplicationMaster.class,
                        log4jPath);
            } catch (Exception e) {
                logger.warn("Can not set up custom log4j properties. " + e);
            }
        }

        if (cliParser.hasOption("help")) {
            printUsage(opts);
            return false;
        }

        if (cliParser.hasOption("debug")) {
            dumpOutDebugInfo();
        }

        Map<String, String> envs = System.getenv();

        if (!envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.name())) {
            if (cliParser.hasOption("app_attempt_id")) {
                String appIdStr = cliParser.getOptionValue("app_attempt_id", "");
                appAttemptID = ConverterUtils.toApplicationAttemptId(appIdStr);
            } else {
                throw new IllegalArgumentException(
                        "Application Attempt Id not set in the environment");
            }
        } else {
            ContainerId containerId = ConverterUtils.toContainerId(envs
                    .get(ApplicationConstants.Environment.CONTAINER_ID.name()));
            appAttemptID = containerId.getApplicationAttemptId();
        }

        if (!envs.containsKey(ApplicationConstants.APP_SUBMIT_TIME_ENV)) {
            throw new RuntimeException(ApplicationConstants.APP_SUBMIT_TIME_ENV
                    + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HOST.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_HOST.name()
                    + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HTTP_PORT.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_HTTP_PORT
                    + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_PORT.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_PORT.name()
                    + " not set in the environment");
        }

        deployId = envs.get(EtlOnYarnContants.DEPLOY_ID_STRING);
        //deployConfigJson = envs.get(EtlOnYarnContants.DEPLOY_CONFIG_JSON_STRING);
        runInstanceId = envs.get(EtlOnYarnContants.RUN_INSTANCE_ID_STRING);

        logger.info("Application master for app" + ", appId="
                + appAttemptID.getApplicationId().getId() + ", clustertimestamp="
                + appAttemptID.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + appAttemptID.getAttemptId());

        if (!fileExist(shellCommandPath)
                && envs.get(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LOCATION).isEmpty()) {
            throw new IllegalArgumentException(
                    "No shell command or shell script specified to be executed by application master");
        }

        if (fileExist(shellCommandPath)) {
            shellCommand = readContent(shellCommandPath);
        }

        if (fileExist(shellArgsPath)) {
            shellArgs = readContent(shellArgsPath);
        }

        if (cliParser.hasOption("shell_env")) {
            String shellEnvs[] = cliParser.getOptionValues("shell_env");
            for (String env : shellEnvs) {
                env = env.trim();
                int index = env.indexOf('=');
                if (index == -1) {
                    shellEnv.put(env, "");
                    continue;
                }
                String key = env.substring(0, index);
                String val = "";
                if (index < (env.length() - 1)) {
                    val = env.substring(index + 1);
                }
                shellEnv.put(key, val);
            }
        }
        // script env info
        if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LOCATION)) {
            scriptPath = envs.get(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LOCATION);

            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_TIMESTAMP)) {
                shellScriptPathTimestamp = Long.parseLong(envs
                        .get(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_TIMESTAMP));
            }
            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LEN)) {
                shellScriptPathLen = Long.parseLong(envs
                        .get(EtlOnYarnContants.ETL_ON_YARN_SHELL_SCRIPT_LEN));
            }
            if (!scriptPath.isEmpty()
                    && (shellScriptPathTimestamp <= 0 || shellScriptPathLen <= 0)) {
                logger.error("Illegal values in env for shell script path" + ", path="
                        + scriptPath + ", len=" + shellScriptPathLen + ", timestamp="
                        + shellScriptPathTimestamp);
                throw new IllegalArgumentException(
                        "Illegal values in env for shell script path");
            }
        }

        if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_SHELL_TIMELINE_DOMAIN)) {
            domainId = envs.get(EtlOnYarnContants.ETL_ON_YARN_SHELL_TIMELINE_DOMAIN);
        }

        // jar file env info
        if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LOCATION)) {
            jarPath = envs.get(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LOCATION);

            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LEN)) {
                jarPathLen = Long.parseLong(envs.get(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_LEN));
            }

            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_TIMESTAMP)) {
                jarPathTimestamp = Long.parseLong(envs.get(EtlOnYarnContants.ETL_ON_YARN_JAR_FILE_TIMESTAMP));
            }

            if (!jarPath.isEmpty()
                    && (jarPathTimestamp <= 0 || jarPathLen <= 0)) {
                logger.error("Illegal values in env for jar file path" + ", path="
                        + jarPath + ", len=" + jarPathLen + ", timestamp="
                        + jarPathTimestamp);
                throw new IllegalArgumentException(
                        "Illegal values in env for jar file path");
            }
        }

        // deploy file env info

        if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LOCATION)) {
            deployFilePath = envs.get(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LOCATION);

            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LEN)) {
                deployFilePathLen = Long.parseLong(envs.get(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_LEN));
            }

            if (envs.containsKey(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_TIMESTAMP)) {
                deployFilePathTimestamp = Long.parseLong(envs.get(EtlOnYarnContants.ETL_ON_YARN_DEPLOY_FILE_TIMESTAMP));
            }

            if (!deployFilePath.isEmpty()
                    && (deployFilePathTimestamp <= 0 || deployFilePathLen <= 0)) {
                logger.error("Illegal values in env for deploy file path" + ", path="
                        + deployFilePath + ", len=" + deployFilePathLen + ", timestamp="
                        + deployFilePathTimestamp);
                throw new IllegalArgumentException(
                        "Illegal values in env for deploy file path");
            }
        }

        containerMemory = Integer.parseInt(cliParser.getOptionValue(
                "container_memory", "10"));
        containerVirtualCores = Integer.parseInt(cliParser.getOptionValue(
                "container_vcores", "1"));
        numTotalContainers = Integer.parseInt(cliParser.getOptionValue(
                "num_containers", "1"));
        if (numTotalContainers == 0) {
            throw new IllegalArgumentException(
                    "Cannot run distributed shell with no containers");
        }
        requestPriority = Integer.parseInt(cliParser
                .getOptionValue("priority", "0"));
        return true;
    }

    /**
     * Helper function to print usage
     *
     * @param opts Parsed command line options
     */
    private void printUsage(Options opts) {
        new HelpFormatter().printHelp("ApplicationMaster", opts);
    }

    private boolean fileExist(String filePath) {
        return new File(filePath).exists();
    }

    private void dumpOutDebugInfo() {

        logger.info("Dump debug output");
        Map<String, String> envs = System.getenv();
        for (Map.Entry<String, String> env : envs.entrySet()) {
            logger.info("System env: key=" + env.getKey() + ", val=" + env.getValue());
            System.out.println("System env: key=" + env.getKey() + ", val="
                    + env.getValue());
        }

        BufferedReader buf = null;
        try {
            String lines = Shell.WINDOWS ? Shell.execCommand("cmd", "/c", "dir") :
                    Shell.execCommand("ls", "-al");
            buf = new BufferedReader(new StringReader(lines));
            String line = "";
            while ((line = buf.readLine()) != null) {
                logger.info("System CWD content: " + line);
                System.out.println("System CWD content: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.cleanup((Log) logger, buf);
        }
    }

    private String readContent(String filePath) throws IOException {
        DataInputStream ds = null;
        try {
            ds = new DataInputStream(new FileInputStream(filePath));
            return ds.readUTF();
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(ds);
        }
    }

    static class NMCallbackHandler
            implements NMClientAsync.CallbackHandler {

        private ConcurrentMap<ContainerId, Container> containers =
                new ConcurrentHashMap<ContainerId, Container>();
        private final EtlApplicationMaster applicationMaster;

        public NMCallbackHandler(EtlApplicationMaster applicationMaster) {
            this.applicationMaster = applicationMaster;
        }

        public void addContainer(ContainerId containerId, Container container) {
            containers.putIfAbsent(containerId, container);
        }

        @Override
        public void onContainerStopped(ContainerId containerId) {
            if (logger.isDebugEnabled()) {
                logger.debug("Succeeded to stop Container " + containerId);
            }
            containers.remove(containerId);
        }

        @Override
        public void onContainerStatusReceived(ContainerId containerId,
                                              ContainerStatus containerStatus) {
            if (logger.isDebugEnabled()) {
                logger.debug("Container Status: id=" + containerId + ", status=" +
                        containerStatus);
            }
        }

        @Override
        public void onContainerStarted(ContainerId containerId,
                                       Map<String, ByteBuffer> allServiceResponse) {
            if (logger.isDebugEnabled()) {
                logger.debug("Succeeded to start Container " + containerId);
            }
            Container container = containers.get(containerId);
            if (container != null) {
                applicationMaster.nmClientAsync.getContainerStatusAsync(containerId, container.getNodeId());
            }
            if(applicationMaster.timelineClient != null) {
                EtlApplicationMaster.publishContainerStartEvent(
                        applicationMaster.timelineClient, container,
                        applicationMaster.domainId, applicationMaster.appSubmitterUgi);
            }
        }

        @Override
        public void onStartContainerError(ContainerId containerId, Throwable t) {
            logger.error("Failed to start Container " + containerId);
            containers.remove(containerId);
            applicationMaster.numCompletedContainers.incrementAndGet();
            applicationMaster.numFailedContainers.incrementAndGet();
        }

        @Override
        public void onGetContainerStatusError(
                ContainerId containerId, Throwable t) {
            logger.error("Failed to query the status of Container " + containerId);
        }

        @Override
        public void onStopContainerError(ContainerId containerId, Throwable t) {
            logger.error("Failed to stop Container " + containerId);
            containers.remove(containerId);
        }
    }

    private static void publishContainerStartEvent(
            final TimelineClient timelineClient, Container container, String domainId,
            UserGroupInformation ugi) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityId(container.getId().toString());
        entity.setEntityType(ETLEntity.ETL_CONTAINER.toString());
        entity.setDomainId(domainId);
        entity.addPrimaryFilter("user", ugi.getShortUserName());
        TimelineEvent event = new TimelineEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setEventType(ETLEvent.ETL_CONTAINER_START.toString());
        event.addEventInfo("Node", container.getNodeId().toString());
        event.addEventInfo("Resources", container.getResource().toString());
        entity.addEvent(event);

        try {
            ugi.doAs(new PrivilegedExceptionAction<TimelinePutResponse>() {
                @Override
                public TimelinePutResponse run() throws Exception {
                    return timelineClient.putEntities(entity);
                }
            });
        } catch (Exception e) {
            logger.error("Container start event could not be published for "
                            + container.getId().toString(),
                    e instanceof UndeclaredThrowableException ? e.getCause() : e);
        }
    }

    private void renameScriptFile(final Path renamedScriptPath)
            throws IOException, InterruptedException {
        appSubmitterUgi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws IOException {
                FileSystem fs = renamedScriptPath.getFileSystem(conf);
                fs.rename(new Path(scriptPath), renamedScriptPath);
                return null;
            }
        });
        logger.info("User " + appSubmitterUgi.getUserName()
                + " added suffix(.sh/.bat) to script file as " + renamedScriptPath);
    }
}
