package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1ContainerState
import io.kubernetes.client.models.V1ContainerStateRunning
import io.kubernetes.client.models.V1ContainerStateTerminated
import io.kubernetes.client.models.V1ContainerStateWaiting
import org.joda.time.DateTime
import spock.lang.Specification

class ContainerStateSpec extends Specification {
    def "create a ContainerState with an empty kube state object"() {
        given:
        V1ContainerState containerState = new V1ContainerState()

        when:
        def status = new ContainerState(containerState)

        then:
        status.state == "Waiting"
    }

    def "create a ContainerState with a running state"() {
        given:
        V1ContainerState containerState = new V1ContainerState()
        V1ContainerStateRunning running = new V1ContainerStateRunning()
        DateTime dt = DateTime.now()
        running.setStartedAt(dt)
        containerState.setRunning(running)

        when:
        def status = new ContainerState(containerState)

        then:
        status.state == "Running"
        status.runningStarted == dt
    }

    def "create a ContainerState with a waiting state"() {
        given:
        V1ContainerState containerState = new V1ContainerState()
        V1ContainerStateWaiting waiting = new V1ContainerStateWaiting()
        waiting.setReason("Just because")
        containerState.setWaiting(waiting)

        when:
        def status = new ContainerState(containerState)

        then:
        status.state == "Waiting"
        status.waitingReason == "Just because"
    }

    def "create a ContainerState with a terminated state"() {
        given:
        V1ContainerState containerState = new V1ContainerState()
        V1ContainerStateTerminated terminated = new V1ContainerStateTerminated()
        terminated.setReason("Just because")
        terminated.setMessage("It's dead Jim")
        terminated.setExitCode(-1)
        terminated.setSignal(15)
        DateTime start = DateTime.now().minusDays(1)
        terminated.setStartedAt(start)
        DateTime end = DateTime.now()
        terminated.setFinishedAt(end)
        containerState.setTerminated(terminated)

        when:
        def status = new ContainerState(containerState)

        then:
        status.state == "Terminated"
        status.terminatedReason == "Just because"
        status.terminatedMessage == "It's dead Jim"
        status.terminatedExitCode == -1
        status.terminatedSignal == 15
        status.terminatedStarted == start
        status.terminatedFinished == end
    }
}
