package org.vogel.kubernetes.dashboard.deployment

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.models.*
import org.vogel.kubernetes.dashboard.deployment.Deployment
import spock.lang.Specification

class DeploymentSpec extends Specification {
    def "create a Deployment pretty empty"() {
        given:
        V1beta2Deployment kubeDeploy = Mock(V1beta2Deployment)
        def metadata = Mock(V1ObjectMeta)
        kubeDeploy.metadata >> metadata
        def spec = Mock(V1beta2DeploymentSpec)
        spec.replicas >> 1
        V1beta2DeploymentStrategy strategy = new V1beta2DeploymentStrategy()
        strategy.setType("foo")
        spec.strategy >> strategy
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        kubePodTemplateSpec.metadata >> metadata
        def templateSpec = Mock(V1PodSpec)
        kubePodTemplateSpec.spec >> templateSpec
        List<V1Container> containers = new ArrayList<>()
        def kubeContainer1 = Mock(V1Container)
        def resourceRequirementsKubeContainer1 = Mock(V1ResourceRequirements)
        kubeContainer1.resources >> resourceRequirementsKubeContainer1
        containers << kubeContainer1
        templateSpec.containers >> containers
        spec.template >> kubePodTemplateSpec
        kubeDeploy.spec >> spec
        def status = Mock(V1beta2DeploymentStatus)
        kubeDeploy.status >> status

        when:
        def deployment = new Deployment(kubeDeploy)

        then:
        deployment.desired == 1
        deployment.current == 0
        deployment.updated == 0
        deployment.available == 0
        deployment.selector == ""
        deployment.unavailable == 0
        deployment.strategyType == "foo"
        deployment.minReadySeconds == 0
        deployment.maxUnavailable == null
        deployment.maxSurge == null
        deployment.podTemplate != null
        deployment.conditions == null
        deployment.oldReplicaSet == null
        deployment.newReplicaSet == null
    }

    def "create a Deployment"() {
        given:
        V1beta2Deployment kubeDeploy = Mock(V1beta2Deployment)
        def metadata = Mock(V1ObjectMeta)
        kubeDeploy.metadata >> metadata
        def spec = Mock(V1beta2DeploymentSpec)
        spec.replicas >> 1
        V1beta2DeploymentStrategy strategy = new V1beta2DeploymentStrategy()
        strategy.setType("foo")
        V1beta2RollingUpdateDeployment rollingUpdate = new V1beta2RollingUpdateDeployment()
        rollingUpdate.setMaxSurge(new IntOrString(2))
        rollingUpdate.setMaxUnavailable(new IntOrString(1))
        strategy.setRollingUpdate(rollingUpdate)
        spec.strategy >> strategy
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        kubePodTemplateSpec.metadata >> metadata
        def templateSpec = Mock(V1PodSpec)
        kubePodTemplateSpec.spec >> templateSpec
        List<V1Container> containers = new ArrayList<>()
        def kubeContainer1 = Mock(V1Container)
        def resourceRequirementsKubeContainer1 = Mock(V1ResourceRequirements)
        kubeContainer1.resources >> resourceRequirementsKubeContainer1
        containers << kubeContainer1
        templateSpec.containers >> containers
        spec.template >> kubePodTemplateSpec
        spec.minReadySeconds >> 5
        kubeDeploy.spec >> spec
        def status = Mock(V1beta2DeploymentStatus)
        status.availableReplicas >> 1
        status.unavailableReplicas >> 1
        List<V1beta2DeploymentCondition> conditions = new ArrayList<>()
        V1beta2DeploymentCondition cond1 = new V1beta2DeploymentCondition()
        cond1.setType("bar")
        cond1.setStatus("good")
        cond1.setReason("because")
        conditions << cond1
        status.conditions >> conditions
        kubeDeploy.status >> status

        when:
        def deployment = new Deployment(kubeDeploy)

        then:
        deployment.desired == 1
        deployment.current == 0
        deployment.updated == 0
        deployment.available == 1
        deployment.selector == ""
        deployment.unavailable == 1
        deployment.strategyType == "foo"
        deployment.minReadySeconds == 5
        deployment.maxUnavailable == "1"
        deployment.maxSurge == "2"
        deployment.podTemplate != null
        deployment.conditions.size() == 1
        deployment.conditions[0].type == "bar"
        deployment.conditions[0].status == "good"
        deployment.conditions[0].reason == "because"
        deployment.oldReplicaSet == null
        deployment.newReplicaSet == null
    }
}
