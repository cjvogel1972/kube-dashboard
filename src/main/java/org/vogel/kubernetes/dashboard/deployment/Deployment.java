package org.vogel.kubernetes.dashboard.deployment;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.vogel.kubernetes.dashboard.Metadata;
import org.vogel.kubernetes.dashboard.PodTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.vogel.kubernetes.dashboard.FormatUtils.formatLabelSelector;

@Getter
public class Deployment extends Metadata {
    private int desired;
    private int current;
    private int updated;
    private int available;
    private String selector;
    private int unavailable;
    private String strategyType;
    private int minReadySeconds;
    private String maxUnavailable;
    private String maxSurge;
    private PodTemplate podTemplate;
    private List<DeploymentCondition> conditions;
    private String oldReplicaSet;
    private String newReplicaSet;

    public Deployment(V1beta2Deployment deployment) {
        super(deployment.getMetadata());
        V1beta2DeploymentSpec deploymentSpec = deployment.getSpec();
        V1beta2DeploymentStatus deploymentStatus = deployment.getStatus();
        desired = deploymentSpec.getReplicas();
        current = defaultIfNull(deploymentStatus.getReplicas(), 0);
        updated = defaultIfNull(deploymentStatus.getUpdatedReplicas(), 0);
        if (deploymentStatus.getAvailableReplicas() != null) {
            available = deploymentStatus.getAvailableReplicas();
        }

        selector = formatLabelSelector(deploymentSpec.getSelector());
        if (deploymentStatus.getUnavailableReplicas() != null) {
            unavailable = deploymentStatus.getUnavailableReplicas();
        }
        strategyType = deploymentSpec.getStrategy()
                .getType();
        if (deploymentSpec.getMinReadySeconds() != null) {
            minReadySeconds = deploymentSpec.getMinReadySeconds();
        }
        V1beta2RollingUpdateDeployment rollingUpdate = deploymentSpec.getStrategy()
                .getRollingUpdate();
        if (rollingUpdate != null) {
            maxUnavailable = rollingUpdate.getMaxUnavailable()
                    .toString();
            maxSurge = rollingUpdate.getMaxSurge()
                    .toString();
        }

        podTemplate = new PodTemplate(deploymentSpec.getTemplate());

        if (isNotEmpty(deploymentStatus.getConditions())) {
            conditions = new ArrayList<>();
            for (V1beta2DeploymentCondition c : deploymentStatus.getConditions()) {
                conditions.add(new DeploymentCondition(c.getType(), c.getStatus(), c.getReason()));
            }
        }
    }

    public void setOldReplicaSet(String oldReplicaSet) {
        this.oldReplicaSet = oldReplicaSet;
    }

    public void setNewReplicaSet(String newReplicaSet) {
        this.newReplicaSet = newReplicaSet;
    }
}