package cn.hhspace.kubernetes.operator.model.v1alpha1;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/3 15:44
 * @Descriptions:
 */
public class PodSetStatus {

    private int availableReplicas;

    public int getAvailableReplicas() {
        return availableReplicas;
    }

    public void setAvailableReplicas(int availableReplicas) {
        this.availableReplicas = availableReplicas;
    }

    @Override
    public String toString() {
        return "PodSetStatus{" +
                "availableReplicas=" + availableReplicas +
                '}';
    }
}
