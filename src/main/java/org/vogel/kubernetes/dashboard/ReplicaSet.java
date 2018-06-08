package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1beta2ReplicaSet;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

@Getter
public class ReplicaSet {
    private String name;
    private int desired;
    private int current;
    private int ready;
    private String age;

    public ReplicaSet(V1beta2ReplicaSet replicaSet) {
        name = replicaSet.getMetadata()
                .getName();
        desired = replicaSet.getSpec()
                .getReplicas();
        current = replicaSet.getStatus()
                .getReplicas();
        ready = replicaSet.getStatus()
                .getReadyReplicas();
        age = translateTimestamp(replicaSet.getMetadata()
                                         .getCreationTimestamp());
    }

    private String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return DurationUtil.shortHumanDuration(duration);
    }
}
