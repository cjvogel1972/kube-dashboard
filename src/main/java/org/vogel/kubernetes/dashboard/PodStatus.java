package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1Pod;
import lombok.Getter;

import java.util.List;

@Getter
public class PodStatus {
    private int running;
    private int waiting;
    private int succeeded;
    private int failed;

    public PodStatus(List<V1Pod> pods, String uid, KubernetesUtils kubeUtil) {
        for (V1Pod pod : pods) {
            if (!kubeUtil.isControlledBy(pod.getMetadata(), uid)) {
                continue;
            }
            String phase = pod.getStatus()
                    .getPhase();
            if ("Running".equals(phase)) {
                running++;
            }
            if ("Pending".equals(phase)) {
                waiting++;
            }
            if ("Succeeded".equals(phase)) {
                succeeded++;
            }
            if ("Failed".equals(phase)) {
                failed++;
            }
        }
    }

}
