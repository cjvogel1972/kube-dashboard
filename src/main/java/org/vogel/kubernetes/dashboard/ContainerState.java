package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ContainerState;
import io.kubernetes.client.models.V1ContainerStateTerminated;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class ContainerState {
    private String state;
    private DateTime runningStarted;
    private String waitingReason;
    private String terminatedReason;
    private String terminatedMessage;
    private Integer terminatedExitCode;
    private Integer terminatedSignal;
    private DateTime terminatedStarted;
    private DateTime terminatedFinished;

    public ContainerState(V1ContainerState containerState) {
        if (containerState.getRunning() != null) {
            state = "Running";
            runningStarted = containerState.getRunning()
                    .getStartedAt();
        } else if (containerState.getWaiting() != null) {
            state = "Waiting";
            waitingReason = containerState.getWaiting()
                    .getReason();
        } else if (containerState.getTerminated() != null) {
            V1ContainerStateTerminated terminated = containerState.getTerminated();
            state = "Terminated";
            terminatedReason = terminated.getReason();
            terminatedMessage = terminated.getMessage();
            terminatedExitCode = terminated.getExitCode();
            terminatedSignal = terminated.getSignal();
            terminatedStarted = terminated.getStartedAt();
            terminatedFinished = terminated.getFinishedAt();
        } else {
            state = "Waiting";
        }
    }
}
