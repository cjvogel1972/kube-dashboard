package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1PersistentVolumeClaimCondition;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class PersistentVolumeClaimCondition {
    private String type;
    private String status;
    private DateTime lastProbeTime;
    private DateTime lastTransitionTime;
    private String reason;
    private String message;

    public PersistentVolumeClaimCondition(V1PersistentVolumeClaimCondition condition) {
        type = condition.getType();
        status = condition.getStatus();
        lastProbeTime = condition.getLastProbeTime();
        lastTransitionTime = condition.getLastTransitionTime();
        reason = condition.getReason();
        message = condition.getMessage();
    }
}
