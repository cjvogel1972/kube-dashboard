package org.vogel.kubernetes.dashboard.replicaset;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.vogel.kubernetes.dashboard.Metadata;
import org.vogel.kubernetes.dashboard.PodStatus;
import org.vogel.kubernetes.dashboard.PodTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.vogel.kubernetes.dashboard.FormatUtils.formatLabelSelector;

@Getter
public class ReplicaSet extends Metadata {
    private int desired;
    private int current;
    private int ready;
    private String selector;
    private String controlledBy;
    private PodStatus status;
    private PodTemplate podTemplate;
    private List<ReplicaSetCondition> conditions;

    public ReplicaSet(V1beta2ReplicaSet replicaSet) {
        super(replicaSet.getMetadata());
        V1ObjectMeta metadata = replicaSet.getMetadata();
        V1beta2ReplicaSetStatus replicaSetStatus = replicaSet.getStatus();
        V1beta2ReplicaSetSpec replicaSetSpec = replicaSet.getSpec();
        desired = replicaSetSpec.getReplicas();
        current = replicaSetStatus.getReplicas();
        if (replicaSetStatus.getReadyReplicas() != null) {
            ready = replicaSetStatus.getReadyReplicas();
        }

        selector = formatLabelSelector(replicaSetSpec.getSelector());
        List<V1OwnerReference> ownerReferences = metadata.getOwnerReferences();
        if (ownerReferences != null) {
            Optional<V1OwnerReference> ownerReference = ownerReferences.stream()
                    .filter(V1OwnerReference::isController)
                    .findFirst();
            if (ownerReference.isPresent()) {
                V1OwnerReference ref = ownerReference.get();
                controlledBy = String.format("%s/%s", ref.getKind(), ref.getName());
            }
        }

        if (isNotEmpty(replicaSetStatus.getConditions())) {
            conditions = new ArrayList<>();
            for (V1beta2ReplicaSetCondition c : replicaSetStatus.getConditions()) {
                conditions.add(new ReplicaSetCondition(c.getType(), c.getStatus(), c.getReason()));
            }
        }

        podTemplate = new PodTemplate(replicaSetSpec.getTemplate());
    }

    public void setStatus(PodStatus status) {
        this.status = status;
    }
}
