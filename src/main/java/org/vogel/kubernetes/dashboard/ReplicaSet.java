package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.vogel.kubernetes.dashboard.FormatUtils.*;

@Getter
public class ReplicaSet {
    private String name;
    private int desired;
    private int current;
    private int ready;
    private String age;
    private String namespace;
    private String selector;
    private List<String> labels;
    private List<String> annotations;
    private String controlledBy;
    private PodStatus status;
    private PodTemplate podTemplate;
    private List<ReplicaSetCondition> conditions;
    private String uid;

    public ReplicaSet(V1beta2ReplicaSet replicaSet) {
        V1ObjectMeta metadata = replicaSet.getMetadata();
        V1beta2ReplicaSetStatus replicaSetStatus = replicaSet.getStatus();
        V1beta2ReplicaSetSpec replicaSetSpec = replicaSet.getSpec();
        name = metadata.getName();
        desired = replicaSetSpec.getReplicas();
        current = replicaSetStatus.getReplicas();
        if (replicaSetStatus.getReadyReplicas() != null) {
            ready = replicaSetStatus.getReadyReplicas();
        }
        age = translateTimestamp(metadata.getCreationTimestamp());

        namespace = metadata.getNamespace();
        selector = formatLabelSelector(replicaSetSpec.getSelector());
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());
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

        if (replicaSetStatus.getConditions() != null && replicaSetStatus.getConditions()
                .size() > 0) {
            conditions = new ArrayList<>();
            for (V1beta2ReplicaSetCondition c : replicaSetStatus.getConditions()) {
                conditions.add(new ReplicaSetCondition(c.getType(), c.getStatus(), c.getReason()));
            }
        }

        podTemplate = new PodTemplate(replicaSetSpec.getTemplate());
        uid = metadata.getUid();
    }

    public void setStatus(PodStatus status) {
        this.status = status;
    }
}
