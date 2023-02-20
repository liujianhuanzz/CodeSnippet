package cn.hhspace.kubernetes.examples;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/10 18:05
 * @Descriptions:
 */
public class ExecuteCommandOnPodExample implements AutoCloseable {
    private final KubernetesClient client;

    public ExecuteCommandOnPodExample() {
        Config config = new ConfigBuilder().build();
        this.client = new KubernetesClientBuilder().withConfig(config).build();
    }

    @Override
    public void close() {
        client.close();
    }

    @SneakyThrows
    public String execCommandOnPod(String podName, String namespace, String... cmd) {
        Pod pod = client.pods().inNamespace(namespace).withName(podName).get();
        System.out.printf("Running command: [%s] on pod [%s] in namespace [%s]%n",
                Arrays.toString(cmd), pod.getMetadata().getName(), namespace);

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch execWatch = execCmd(pod, data, cmd)) {
            return data.get(10, TimeUnit.SECONDS);
        }

    }

    private ExecWatch execCmd(Pod pod, CompletableFuture<String> data, String... command) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return client.pods()
                .inNamespace(pod.getMetadata().getNamespace())
                .withName(pod.getMetadata().getName())
                .writingOutput(baos)
                .writingError(baos)
                .usingListener(new SimpleListener(data, baos))
                .exec(command);
    }

    static class SimpleListener implements ExecListener {

        private CompletableFuture<String> data;
        private ByteArrayOutputStream baos;

        public SimpleListener(CompletableFuture<String> data, ByteArrayOutputStream baos) {
            this.data = data;
            this.baos = baos;
        }

        @Override
        public void onOpen() {
            System.out.println("Reading data... ");
        }

        @Override
        public void onFailure(Throwable t, Response failureResponse) {
            System.err.println(t.getMessage());
            data.completeExceptionally(t);
        }

        @Override
        public void onClose(int code, String reason) {
            System.out.println("Exit with: " + code + " and with reason: " + reason);
            data.complete(baos.toString());
        }
    }

    public static void main(String[] args) {

        final String pod = "operator-test-0 ";
        final String namespace = "ns";
        final String command = "df -h";

        try (ExecuteCommandOnPodExample example = new ExecuteCommandOnPodExample()) {
            String cmdOutput = example.execCommandOnPod(pod, namespace, command.split(" "));
            System.out.println(cmdOutput);
        }

    }
}
