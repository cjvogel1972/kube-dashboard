package org.vogel.kubernetes.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeploymentCondition {
    private String type;
    private String status;
    private String reason;
}
