package cn.hhspace.kubernetes.operator;

import cn.hhspace.kubernetes.operator.controller.PodSetController;
import cn.hhspace.kubernetes.operator.model.v1alpha1.PodSet;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/4 11:24
 * @Descriptions:
 */
public class PodSetOperatorMain {
    public static final Logger logger = LoggerFactory.getLogger(PodSetOperatorMain.class.getSimpleName());

    public static void main(String[] args) {
        try(KubernetesClient client = new KubernetesClientBuilder().build()) {
            String namespace = client.getNamespace();
            if (null == namespace) {
                logger.info("No namespace found via config, assuming default");
                namespace = "default";
            }

            SharedInformerFactory informerFactory = client.informers();
            MixedOperation<PodSet, KubernetesResourceList<PodSet>, Resource<PodSet>> podSetClient = client.resources(PodSet.class);
            SharedIndexInformer<Pod> podSharedIndexInformer = informerFactory.sharedIndexInformerFor(Pod.class, 10 * 60 * 1000L);
            SharedIndexInformer<PodSet> podSetSharedIndexInformer = informerFactory.sharedIndexInformerFor(PodSet.class, 10 * 60 * 1000L);
            PodSetController podSetController = new PodSetController(client, podSetClient, podSharedIndexInformer, podSetSharedIndexInformer, namespace);
            Future<Void> startAllRegisteredInformers = informerFactory.startAllRegisteredInformers();
            startAllRegisteredInformers.get();
            podSetController.run();
        } catch (KubernetesClientException | ExecutionException e) {
            logger.error("Kubernetes Client Exception : ", e);
        } catch (InterruptedException e) {
            logger.error("Interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
