package cn.hhspace.kubernetes.operator.model.v1alpha1;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/1/3 15:40
 * @Descriptions:
 */

@Version("v1alpha1")
@Group("demo.hhspace.cn")
public class PodSet extends CustomResource<PodSetSpec, PodSetStatus> implements Namespaced {
}
