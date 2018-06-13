package org.vogel.kubernetes.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PodStatus {
    private int running;
    private int waiting;
    private int succeeded;
    private int failed;
}
