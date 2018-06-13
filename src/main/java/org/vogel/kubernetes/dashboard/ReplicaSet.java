package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.Nullable;
import java.util.*;

import static java.util.stream.Collectors.toList;

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
    private Map<String, String> conditions;

    public ReplicaSet(V1beta2ReplicaSet replicaSet) {
        V1ObjectMeta metadata = replicaSet.getMetadata();
        V1beta2ReplicaSetStatus replicaSetStatus = replicaSet.getStatus();
        V1beta2ReplicaSetSpec replicaSetSpec = replicaSet.getSpec();
        name = metadata.getName();
        desired = replicaSetSpec.getReplicas();
        current = replicaSetStatus.getReplicas();
        ready = replicaSetStatus.getReadyReplicas();
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
            conditions = new LinkedHashMap<>();
            for (V1beta2ReplicaSetCondition c : replicaSetStatus.getConditions()) {
                conditions.put(c.getType(), c.getStatus());
            }
        }

        podTemplate = new PodTemplate(replicaSetSpec.getTemplate());
    }

    public void setStatus(PodStatus status) {
        this.status = status;
    }

    private String formatLabelSelector(@Nullable V1LabelSelector labelSelector) {
        String result;

        int matchLabelsSize = -1;
        int matchExpressionsSize = -1;
        if (labelSelector != null) {
            if (labelSelector.getMatchLabels() != null) {
                matchLabelsSize = labelSelector.getMatchLabels()
                        .size();
            }
            if (labelSelector.getMatchExpressions() != null) {
                matchExpressionsSize = labelSelector.getMatchExpressions()
                        .size();
            }
        }

        try {
            if (labelSelector == null) {
                result = "";
            } else if (matchLabelsSize + matchExpressionsSize == 0) {
                result = "";
            } else {
                Selector selector = new Selector();
                if (labelSelector.getMatchLabels() != null) {
                    for (Map.Entry<String, String> entry : labelSelector.getMatchLabels()
                            .entrySet()) {
                        List<String> values = Collections.singletonList(entry.getValue());
                        Requirement requirement = new Requirement(entry.getKey(), "=", values);
                        selector.add(requirement);
                    }
                }
                if (labelSelector.getMatchExpressions() != null) {
                    for (V1LabelSelectorRequirement expression : labelSelector.getMatchExpressions()) {
                        String op;
                        switch (expression.getOperator()) {
                            case "In":
                                op = "in";
                                break;
                            case "NotIn":
                                op = "notin";
                                break;
                            case "Exists":
                                op = "exists";
                                break;
                            case "DoesNotExist":
                                op = "!";
                                break;
                            default:
                                String msg = String.format("%s is not a valid pod selector operator",
                                                           expression.getOperator());
                                throw new RequirementException(msg);
                        }
                        Requirement requirement = new Requirement(expression.getKey(), op, expression.getValues());
                        selector.add(requirement);
                    }
                }
                result = selector.string();
                if (result.length() == 0) {
                    result = "<none>";
                }
            }
        } catch (RequirementException e) {
            result = "<error>";
        }

        return result;
    }

    private List<String> printMultiline(Map<String, String> data) {
        List<String> result = null;

        if (data != null && data.size() > 0) {
            result = data.keySet()
                    .stream()
                    .sorted()
                    .map(key -> String.format("%s=%s", key, data.get(key)))
                    .collect(toList());
        }

        return result;
    }

    private String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return DurationUtil.shortHumanDuration(duration);
    }
}
