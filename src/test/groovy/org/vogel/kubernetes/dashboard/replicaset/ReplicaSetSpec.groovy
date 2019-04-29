package org.vogel.kubernetes.dashboard.replicaset

import io.kubernetes.client.models.*
import org.vogel.kubernetes.dashboard.replicaset.ReplicaSet
import spock.lang.Specification

class ReplicaSetSpec extends Specification {
    def "create a ReplicaSet pretty much empty"() {
        given:
        def kubeReplicaSet = Mock(V1beta2ReplicaSet)
        def metadata = Mock(V1ObjectMeta)
        kubeReplicaSet.metadata >> metadata
        def replicaSetSpec = Mock(V1beta2ReplicaSetSpec)
        replicaSetSpec.replicas >> 1
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        def podTemplateSpecMetadata = Mock(V1ObjectMeta)
        kubePodTemplateSpec.metadata >> podTemplateSpecMetadata
        def podTemplateSpecPodSpec = Mock(V1PodSpec)
        List<V1Container> containers = new ArrayList<>()
        podTemplateSpecPodSpec.containers >> containers
        kubePodTemplateSpec.spec >> podTemplateSpecPodSpec
        replicaSetSpec.template >> kubePodTemplateSpec
        kubeReplicaSet.spec >> replicaSetSpec
        def replicaSetStatus = Mock(V1beta2ReplicaSetStatus)
        replicaSetStatus.replicas >> 1
        kubeReplicaSet.status >> replicaSetStatus

        when:
        def replicaSet = new ReplicaSet(kubeReplicaSet)

        then:
        replicaSet.desired == 1
        replicaSet.current == 1
        replicaSet.ready == 0
        replicaSet.selector == ""
        replicaSet.controlledBy == null
        replicaSet.status == null
        replicaSet.podTemplate != null
        replicaSet.podTemplate.labels == null
        replicaSet.podTemplate.annotations == null
        replicaSet.podTemplate.serviceAccountName == null
        replicaSet.podTemplate.initContainers == null
        replicaSet.podTemplate.containers.size() == 0
        replicaSet.podTemplate.volumes.volumes == null
        replicaSet.conditions == null
    }

    def "create a ReplicaSet"() {
        given:
        def kubeReplicaSet = Mock(V1beta2ReplicaSet)
        def metadata = Mock(V1ObjectMeta)
        List<V1OwnerReference> ownerReferences = new ArrayList<>()
        V1OwnerReference ownerReference = new V1OwnerReference()
        ownerReference.setController(true)
        ownerReference.setKind("Deployment")
        ownerReference.setName("kube-dashboard")
        ownerReferences.add(ownerReference)
        metadata.ownerReferences >> ownerReferences
        kubeReplicaSet.metadata >> metadata
        def replicaSetSpec = Mock(V1beta2ReplicaSetSpec)
        replicaSetSpec.replicas >> 1
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        def podTemplateSpecMetadata = Mock(V1ObjectMeta)
        kubePodTemplateSpec.metadata >> podTemplateSpecMetadata
        def podTemplateSpecPodSpec = Mock(V1PodSpec)
        List<V1Container> containers = new ArrayList<>()
        podTemplateSpecPodSpec.containers >> containers
        kubePodTemplateSpec.spec >> podTemplateSpecPodSpec
        replicaSetSpec.template >> kubePodTemplateSpec
        kubeReplicaSet.spec >> replicaSetSpec
        def replicaSetStatus = Mock(V1beta2ReplicaSetStatus)
        replicaSetStatus.replicas >> 1
        replicaSetStatus.readyReplicas >> 1
        List<V1beta2ReplicaSetCondition> conditions = new ArrayList<>()
        V1beta2ReplicaSetCondition condition = new V1beta2ReplicaSetCondition()
        condition.setType("foo")
        condition.setStatus("bar")
        condition.setReason("blah")
        conditions.add(condition)
        replicaSetStatus.conditions >> conditions
        kubeReplicaSet.status >> replicaSetStatus

        when:
        def replicaSet = new ReplicaSet(kubeReplicaSet)

        then:
        replicaSet.desired == 1
        replicaSet.current == 1
        replicaSet.ready == 1
        replicaSet.selector == ""
        replicaSet.controlledBy == "Deployment/kube-dashboard"
        replicaSet.status == null
        replicaSet.podTemplate != null
        replicaSet.podTemplate.labels == null
        replicaSet.podTemplate.annotations == null
        replicaSet.podTemplate.serviceAccountName == null
        replicaSet.podTemplate.initContainers == null
        replicaSet.podTemplate.containers.size() == 0
        replicaSet.podTemplate.volumes.volumes == null
        replicaSet.conditions.size() == 1
        replicaSet.conditions[0].type == "foo"
        replicaSet.conditions[0].status == "bar"
        replicaSet.conditions[0].reason == "blah"
    }

    def "create a ReplicaSet no controller"() {
        given:
        def kubeReplicaSet = Mock(V1beta2ReplicaSet)
        def metadata = Mock(V1ObjectMeta)
        List<V1OwnerReference> ownerReferences = new ArrayList<>()
        metadata.ownerReferences >> ownerReferences
        kubeReplicaSet.metadata >> metadata
        def replicaSetSpec = Mock(V1beta2ReplicaSetSpec)
        replicaSetSpec.replicas >> 1
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        def podTemplateSpecMetadata = Mock(V1ObjectMeta)
        kubePodTemplateSpec.metadata >> podTemplateSpecMetadata
        def podTemplateSpecPodSpec = Mock(V1PodSpec)
        List<V1Container> containers = new ArrayList<>()
        podTemplateSpecPodSpec.containers >> containers
        kubePodTemplateSpec.spec >> podTemplateSpecPodSpec
        replicaSetSpec.template >> kubePodTemplateSpec
        kubeReplicaSet.spec >> replicaSetSpec
        def replicaSetStatus = Mock(V1beta2ReplicaSetStatus)
        replicaSetStatus.replicas >> 1
        replicaSetStatus.readyReplicas >> 1
        kubeReplicaSet.status >> replicaSetStatus

        when:
        def replicaSet = new ReplicaSet(kubeReplicaSet)

        then:
        replicaSet.desired == 1
        replicaSet.current == 1
        replicaSet.ready == 1
        replicaSet.selector == ""
        replicaSet.controlledBy == null
        replicaSet.status == null
        replicaSet.podTemplate != null
        replicaSet.podTemplate.labels == null
        replicaSet.podTemplate.annotations == null
        replicaSet.podTemplate.serviceAccountName == null
        replicaSet.podTemplate.initContainers == null
        replicaSet.podTemplate.containers.size() == 0
        replicaSet.podTemplate.volumes.volumes == null
        replicaSet.conditions == null
    }
}
