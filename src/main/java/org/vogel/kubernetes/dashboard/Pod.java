package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;

@Getter
public class Pod {
    private String name;
    private String ready;
    private String reason;
    private int restarts;
    private String age;
    private String namespace;
    private Integer priority;
    private String priorityClassName;
    private String node;
    private String hostIp;
    private DateTime startTime;
    private List<String> labels;
    private List<String> annotations;
    private DateTime deletionTimestamp;
    private String deletionDuration;
    private long deletionGracePeriodSeconds;
    private String status;
    private String describeReason;
    private String message;
    private String podIp;
    private String controlledBy;

    public Pod(V1Pod pod) {
        restarts = 0;
        V1PodSpec podSpec = pod.getSpec();
        int totalContainers = podSpec.getContainers()
                .size();
        int readyContainers = 0;

        V1PodStatus podStatus = pod.getStatus();
        reason = podStatus.getPhase();
        if (isNotBlank(podStatus.getReason())) {
            reason = podStatus.getReason();
        }

        boolean initializing = false;
        List<V1ContainerStatus> initContainerStatuses = podStatus.getInitContainerStatuses();
        if (initContainerStatuses != null) {
            for (int i = 0; i < initContainerStatuses.size(); i++) {
                V1ContainerStatus container = initContainerStatuses.get(i);
                restarts += container.getRestartCount();

                V1ContainerState containerState = container.getState();
                V1ContainerStateTerminated terminated = containerState.getTerminated();
                V1ContainerStateWaiting waiting = containerState.getWaiting();
                if (terminated != null && terminated.getExitCode() == 0) {
                    continue;
                } else if (terminated != null) {
                    // initialization is failed
                    if (isBlank(terminated.getReason())) {
                        if (terminated.getSignal() != 0) {
                            reason = String.format("Init:Signal:%d", terminated.getSignal());
                        } else {
                            reason = String.format("Init:ExitCode:%d", terminated.getExitCode());
                        }
                    } else {
                        reason = "Init:" + terminated.getReason();
                    }
                    initializing = true;
                } else if (waiting != null && isNotBlank(waiting.getReason()) && !waiting.getReason()
                        .equals("PodInitializing")) {
                    reason = "Init:" + waiting.getReason();
                    initializing = true;
                } else {
                    reason = String.format("Init:%d/%d", i, podSpec.getInitContainers()
                            .size());
                    initializing = true;
                }
                break;
            }
        }

        if (!initializing) {
            List<V1ContainerStatus> containerStatuses = podStatus.getContainerStatuses();
            restarts = 0;
            boolean hasRunning = false;
            for (int i = containerStatuses.size() - 1; i >= 0; i--) {
                V1ContainerStatus container = containerStatuses.get(i);

                restarts += container.getRestartCount();
                V1ContainerState containerState = container.getState();
                V1ContainerStateTerminated terminated = containerState.getTerminated();
                V1ContainerStateWaiting waiting = containerState.getWaiting();
                if (waiting != null && isNotEmpty(waiting.getReason())) {
                    reason = waiting.getReason();
                } else if (terminated != null && isNotEmpty(terminated.getReason())) {
                    reason = terminated.getReason();
                } else if (terminated != null && isEmpty(terminated.getReason())) {
                    if (terminated.getSignal() != 0) {
                        reason = String.format("Signal:%d", terminated.getSignal());
                    } else {
                        reason = String.format("ExitCode:%d", terminated.getExitCode());
                    }
                } else if (container.isReady() && containerState.getRunning() != null) {
                    hasRunning = true;
                    readyContainers++;
                }

                // change pod status back to "Running" if there is at least one container still reporting as "Running" status
                if (reason.equals("Completed") && hasRunning) {
                    reason = "Running";
                }
            }
        }

        V1ObjectMeta metadata = pod.getMetadata();
        deletionTimestamp = metadata.getDeletionTimestamp();
        if (deletionTimestamp != null && podStatus.getReason()
                .equals("NodeLost")) {
            reason = "Unknown";
        } else if (deletionTimestamp != null) {
            reason = "Terminating";
        }

        name = metadata.getName();
        ready = String.format("%d/%d", readyContainers, totalContainers);
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);

        namespace = metadata.getNamespace();
        priority = podSpec.getPriority();
        if (priority != null) {
            priorityClassName = podSpec.getPriorityClassName();
        }
        node = podSpec.getNodeName();
        hostIp = podStatus.getHostIP();
        startTime = podStatus.getStartTime();
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());

        if (deletionTimestamp != null) {
            deletionDuration = translateTimestamp(deletionTimestamp);
            deletionGracePeriodSeconds = metadata.getDeletionGracePeriodSeconds();
        }
        status = podStatus.getPhase();
        describeReason = podStatus.getReason();
        message = podStatus.getMessage();
        podIp = podStatus.getPodIP();
        List<V1OwnerReference> ownerReferences = metadata.getOwnerReferences();
        Optional<V1OwnerReference> ownerReference = ownerReferences.stream()
                .filter(V1OwnerReference::isController)
                .findFirst();
        if (ownerReference.isPresent()) {
            V1OwnerReference ref = ownerReference.get();
            controlledBy = String.format("%s/%s", ref.getKind(), ref.getName());
        }
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
