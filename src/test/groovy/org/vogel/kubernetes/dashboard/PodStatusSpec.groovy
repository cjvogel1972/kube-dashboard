package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1ObjectMeta
import io.kubernetes.client.models.V1OwnerReference
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1PodStatus
import spock.lang.Specification

class PodStatusSpec extends Specification {
    def "create a PodStatus"() {
        given:
        List<V1Pod> pods = new ArrayList<>()
        V1Pod pod1 = createPod("1234", "Running")
        pods << pod1
        V1Pod pod2 = createPod("1234", "Pending")
        pods << pod2
        V1Pod pod3 = createPod("1234", "Succeeded")
        pods << pod3
        V1Pod pod4 = createPod("1234", "Failed")
        pods << pod4
        V1Pod pod5 = createPod("5678", "Running")
        pods << pod5
        KubernetesUtils kubeUtils = new KubernetesUtils()

        when:
        def podStatus = new PodStatus(pods, "1234", kubeUtils)

        then:
        podStatus.running == 1
        podStatus.waiting == 1
        podStatus.succeeded == 1
        podStatus.failed == 1
    }

    private V1Pod createPod(def uid, def phase) {
        V1Pod pod1 = Mock(V1Pod)
        V1ObjectMeta pod1Meta = Mock(V1ObjectMeta)
        List<V1OwnerReference> ownerReferences = new ArrayList<>()
        V1OwnerReference pod1OwnerRef1 = Mock(V1OwnerReference)
        pod1OwnerRef1.isController() >> Boolean.TRUE
        pod1OwnerRef1.uid >> uid
        ownerReferences << pod1OwnerRef1
        pod1Meta.ownerReferences >> ownerReferences
        pod1.metadata >> pod1Meta
        V1PodStatus pod1Status = Mock(V1PodStatus)
        pod1Status.phase >> phase
        pod1.status >> pod1Status
        pod1
    }
}
