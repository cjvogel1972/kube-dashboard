package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.vogel.kubernetes.dashboard.FormatUtils.*;

@Getter
public class Deployment {
    private String name;
    private int desired;
    private int current;
    private int updated;
    private int available;
    private String age;
    private String namespace;
    private DateTime creationTimestamp;
    private List<String> labels;
    private List<String> annotations;
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
    private String uid;

    public Deployment(V1beta2Deployment deployment) {
        V1ObjectMeta metadata = deployment.getMetadata();
        V1beta2DeploymentSpec deploymentSpec = deployment.getSpec();
        V1beta2DeploymentStatus deploymentStatus = deployment.getStatus();
        name = metadata.getName();
        desired = deploymentSpec.getReplicas();
        current = deploymentStatus.getReplicas();
        updated = deploymentStatus.getUpdatedReplicas();
        if (deploymentStatus.getAvailableReplicas() != null) {
            available = deploymentStatus.getAvailableReplicas();
        }
        creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);

        namespace = metadata.getNamespace();
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());
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

        if (deploymentStatus.getConditions() != null && deploymentStatus.getConditions()
                .size() > 0) {
            conditions = new ArrayList<>();
            for (V1beta2DeploymentCondition c : deploymentStatus.getConditions()) {
                conditions.add(new DeploymentCondition(c.getType(), c.getStatus(), c.getReason()));
            }
        }
        uid = metadata.getUid();
    }

    public void setOldReplicaSet(String oldReplicaSet) {
        this.oldReplicaSet = oldReplicaSet;
    }

    public void setNewReplicaSet(String newReplicaSet) {
        this.newReplicaSet = newReplicaSet;
    }
}