package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1beta2Deployment;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

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
        name = metadata.getName();
        desired = deployment.getSpec()
                .getReplicas();
        current = deployment.getStatus()
                .getReplicas();
        updated = deployment.getStatus()
                .getUpdatedReplicas();
        available = deployment.getStatus()
                .getAvailableReplicas();
        age = translateTimestamp(metadata.getCreationTimestamp());
    }

    private String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return DurationUtil.shortHumanDuration(duration);
    }
}
