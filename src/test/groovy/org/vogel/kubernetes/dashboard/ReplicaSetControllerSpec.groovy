package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class ReplicaSetControllerSpec extends Specification {
    def "test getting a list of ReplicaSets"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultReplicaSet = Mock(ReplicaSet)
        def resultList = [resultReplicaSet]
        kubeUtil.getReplicaSets("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new ReplicaSetController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listReplicaSets(model, "default")

        then:
        result == "replica_sets"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("replicaSets", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of ReplicaSets with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getReplicaSets("default") >> { throw new ApiException() }
        def controller = new ReplicaSetController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listReplicaSets(model, "default")

        then:
        result == "error"
    }

    def "test describing a ReplicaSet"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultReplicaSet = Mock(ReplicaSet)
        kubeUtil.getReplicaSet("default", "my-replica-set") >> resultReplicaSet
        def controller = new ReplicaSetController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeReplicaSet(model, "default", "my-replica-set")

        then:
        result == "replica_set_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("replicaSet", resultReplicaSet)
        1 * model.addAttribute("replicaSetName", "my-replica-set")
    }

    def "test describing a ReplicaSet with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getReplicaSet("default", "my-replica-set") >> { throw new ApiException() }
        def controller = new ReplicaSetController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeReplicaSet(model, "default", "my-replica-set")

        then:
        result == "error"
    }
}
