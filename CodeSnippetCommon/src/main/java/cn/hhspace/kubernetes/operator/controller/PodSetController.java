package cn.hhspace.kubernetes.operator.controller;

import cn.hhspace.kubernetes.operator.model.v1alpha1.PodSet;
import cn.hhspace.kubernetes.operator.model.v1alpha1.PodSetStatus;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Cache;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/3 15:47
 * @Descriptions:
 */
public class PodSetController {

    private KubernetesClient kubernetesClient;

    private MixedOperation<PodSet, KubernetesResourceList<PodSet>, Resource<PodSet>> podSetClient;

    private SharedIndexInformer<Pod> podInformer;

    private SharedIndexInformer<PodSet> podSetInformer;

    private BlockingQueue<String> workQueue;

    private Lister<PodSet> podSetLister;

    private Lister<Pod> podLister;

    public static final Logger logger = LoggerFactory.getLogger(PodSetController.class.getSimpleName());

    public static final String APP_LABEL = "app";

    public PodSetController(KubernetesClient kubernetesClient, MixedOperation<PodSet, KubernetesResourceList<PodSet>, Resource<PodSet>> podSetClient,
                            SharedIndexInformer<Pod> podInformer, SharedIndexInformer<PodSet> podSetInformer,
                            String namespace) {
        this.kubernetesClient = kubernetesClient;
        this.podSetClient = podSetClient;
        this.podInformer = podInformer;
        this.podSetInformer = podSetInformer;
        this.podSetLister = new Lister<>(podSetInformer.getIndexer(), namespace);
        this.podLister = new Lister<>(podInformer.getIndexer(), namespace);
        this.workQueue = new ArrayBlockingQueue<>(1024);
        addEventHandlersToSharedIndexInformers();
    }

    public void run() {
        logger.info("Starting PodSet controller");
        while (!Thread.currentThread().isInterrupted()) {
            if (podInformer.hasSynced() && podSetInformer.hasSynced()) {
                break;
            }
        }

        while (true) {
            try {
                logger.info("try to fetch item from workQueue");
                if (workQueue.isEmpty()) {
                    logger.info("workQueue is empty");
                }

                String key = workQueue.take();
                Objects.requireNonNull(key, "key can't be null");
                logger.info("Got {}", key);
                if (!key.contains("/")) {
                    logger.warn("invalid resource key: {}", key);
                }

                String name = key.split("/")[1];
                PodSet podSet = podSetLister.get(name);
                if (null == podSet) {
                    logger.error("PodSet {} in workQueue no longer exists", name);
                    return;
                }

                reconcile(podSet);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("controller interrupted");
            }
        }
    }

    protected void reconcile(PodSet podSet) throws InterruptedException {
        List<String> pods = podCountByLabel(APP_LABEL, podSet.getMetadata().getName());
        logger.info("reconcile() : Found {} number of Pods owned by PodSet {}", pods.size(), podSet.getMetadata().getName());
        if (pods.isEmpty()) {
            createPods(podSet.getSpec().getReplicas(), podSet);
            return;
        }

        int existingPods = pods.size();

        if (existingPods < podSet.getSpec().getReplicas()) {
            createPods(podSet.getSpec().getReplicas() - existingPods, podSet);
        }

        int diff = existingPods - podSet.getSpec().getReplicas();
        for (; diff > 0; diff--) {
            String podName = pods.remove(0);
            kubernetesClient.pods().inNamespace(podSet.getMetadata().getNamespace()).withName(podName).delete();
        }

        updateAvailableReplicasInPodSetStatus(podSet, podSet.getSpec().getReplicas());
    }

    private void updateAvailableReplicasInPodSetStatus(PodSet podSet, int replicas) {
        PodSetStatus podSetStatus = new PodSetStatus();
        podSetStatus.setAvailableReplicas(replicas);
        podSet.setStatus(podSetStatus);
        podSetClient.inNamespace(podSet.getMetadata().getNamespace()).resource(podSet).replaceStatus();
    }

    private void addEventHandlersToSharedIndexInformers() {
        podSetInformer.addEventHandler(new ResourceEventHandler<PodSet>() {
            @Override
            public void onAdd(PodSet podSet) {
                logger.info("PodSet {} ADDED", podSet.getMetadata().getName());
                enqueuePodSet(podSet);
            }

            @Override
            public void onUpdate(PodSet podSet, PodSet t1) {
                logger.info("PodSet {} MODIFIED", podSet.getMetadata().getName());
                enqueuePodSet(podSet);
            }

            @Override
            public void onDelete(PodSet podSet, boolean b) {
                //Do nothing
            }
        });

        podInformer.addEventHandler(new ResourceEventHandler<Pod>() {
            @Override
            public void onAdd(Pod pod) {
                handlePodObject(pod);
            }

            @Override
            public void onUpdate(Pod oldPod, Pod newPod) {
                if (oldPod.getMetadata().getResourceVersion().equals(newPod.getMetadata().getResourceVersion())) {
                    return;
                }
                handlePodObject(newPod);
            }

            @Override
            public void onDelete(Pod pod, boolean b) {
                //Do nothing
            }
        });
    }

    private void createPods(int numberOfPods, PodSet podSet) throws InterruptedException {
        for (int index = 0; index < numberOfPods; index++) {
            Pod pod = createNewPod(podSet);
            pod = kubernetesClient.pods().inNamespace(podSet.getMetadata().getNamespace()).resource(pod).create();
            kubernetesClient.pods().inNamespace(podSet.getMetadata().getNamespace())
                    .withName(pod.getMetadata().getName())
                    .waitUntilCondition(Objects::nonNull, 3, TimeUnit.SECONDS);
        }
        logger.info("Created {} pods for {} PodSet", numberOfPods, podSet.getMetadata().getName());
    }

    private List<String> podCountByLabel(String label, String podSetName) {
        List<String> podNames = new ArrayList<>();
        List<Pod> pods = podLister.list();

        for (Pod pod : pods) {
            if (pod.getMetadata().getLabels().entrySet().contains(new AbstractMap.SimpleEntry<>(label, podSetName))) {
                if (pod.getStatus().getPhase().equals("Running") || pod.getStatus().getPhase().equals("Pending")) {
                    podNames.add(pod.getMetadata().getName());
                }
            }
        }

        logger.info("count: {}", podNames.size());
        return podNames;
    }

    private Pod createNewPod(PodSet podSet) {
        return new PodBuilder()
                .withNewMetadata()
                    .withGenerateName(podSet.getMetadata().getName() + "-pod")
                    .withNamespace(podSet.getMetadata().getNamespace())
                    .withLabels(Collections.singletonMap(APP_LABEL, podSet.getMetadata().getName()))
                    .addNewOwnerReference()
                        .withController(Boolean.TRUE)
                        .withKind("PodSet")
                        .withApiVersion("demo.hhspace.cn/v1alpha1")
                        .withName(podSet.getMetadata().getName())
                        .withUid(podSet.getMetadata().getUid())
                    .endOwnerReference()
                .endMetadata()
                .withNewSpec()
                    .addNewContainer()
                        .withName("busybox")
                        .withImage("busybox")
                        .withCommand("sleep", "6000")
                    .endContainer()
                .endSpec()
                .build();
    }

    private void enqueuePodSet(PodSet podSet) {
        logger.info("enqueuePodSet({})", podSet.getMetadata().getName());
        String key = Cache.metaNamespaceKeyFunc(podSet);
        logger.info("Going to enqueue key {}", key);
        if (null != key && !key.isEmpty()) {
            logger.info("Adding item to workqueue");
            workQueue.add(key);
        }
    }

    private void handlePodObject(Pod pod) {
        logger.info("handlePodObject({})", pod.getMetadata().getName());
        OwnerReference ownerReference = getControllerOf(pod);
        if (null == ownerReference || !ownerReference.getKind().equalsIgnoreCase("PodSet")) {
            return;
        }
        PodSet podSet = podSetLister.get(ownerReference.getName());
        logger.info("PodSetLister returned {} for PodSet", podSet);
        if (null != podSet) {
            enqueuePodSet(podSet);
        }
    }

    private OwnerReference getControllerOf(Pod pod) {
        List<OwnerReference> ownerReferences = pod.getMetadata().getOwnerReferences();
        for (OwnerReference ownerReference : ownerReferences) {
            if (ownerReference.getController().equals(Boolean.TRUE)) {
                return ownerReference;
            }
        }
        return null;
    }
}
