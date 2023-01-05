package cn.hhspace.kubernetes.operator.model.v1alpha1;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/3 15:42
 * @Descriptions:
 */
public class PodSetSpec {
    private int replicas;

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    @Override
    public String toString() {
        return "PodSetSpec{" +
                "replicas=" + replicas +
                '}';
    }
}
