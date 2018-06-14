package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1beta2Deployment;
import io.kubernetes.client.models.V1beta2DeploymentSpec;
import io.kubernetes.client.models.V1beta2DeploymentStatus;
import lombok.Getter;

import static org.vogel.kubernetes.dashboard.DurationUtil.translateTimestamp;

@Getter
public class Deployment {
    private String name;
    private int desired;
    private int current;
    private int updated;
    private int available;
    private String age;

    public Deployment(V1beta2Deployment deployment) {
        V1ObjectMeta metadata = deployment.getMetadata();
        V1beta2DeploymentSpec deploymentSpec = deployment.getSpec();
        V1beta2DeploymentStatus deploymentStatus = deployment.getStatus();
        name = metadata.getName();
        desired = deploymentSpec.getReplicas();
        current = deploymentStatus.getReplicas();
        updated = deploymentStatus.getUpdatedReplicas();
        available = deploymentStatus.getAvailableReplicas();
        age = translateTimestamp(metadata.getCreationTimestamp());
    }
}
