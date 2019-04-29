package org.vogel.kubernetes.dashboard.replicaset;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReplicaSetCondition {
    private String type;
    private String status;
    private String reason;
}
