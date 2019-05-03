package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.*
import spock.lang.Specification

class PodTemplateSpec extends Specification {
    def "create a PodTempate"() {
        given:
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        def metadata = Mock(V1ObjectMeta)
        kubePodTemplateSpec.metadata >> metadata
        def spec = Mock(V1PodSpec)
        spec.serviceAccountName >> "serviceAcctName"
        List<V1Container> containers = new ArrayList<>()
        def kubeContainer1 = Mock(V1Container)
        def resourceRequirementsKubeContainer1 = Mock(V1ResourceRequirements)
        kubeContainer1.resources >> resourceRequirementsKubeContainer1
        containers << kubeContainer1
        spec.containers >> containers
        List<V1Container> initContainers = new ArrayList<>()
        def kubeInitContainer1 = Mock(V1Container)
        def resourceRequirementsKubeInitContainer1 = Mock(V1ResourceRequirements)
        kubeInitContainer1.resources >> resourceRequirementsKubeInitContainer1
        initContainers << kubeInitContainer1
        spec.initContainers >> initContainers
        kubePodTemplateSpec.spec >> spec

        when:
        def podTemplateSpec = new PodTemplate(kubePodTemplateSpec)

        then:
        podTemplateSpec.labels == null
        podTemplateSpec.annotations == null
        podTemplateSpec.serviceAccountName == "serviceAcctName"
        podTemplateSpec.initContainers.size() == 1
        podTemplateSpec.containers.size() == 1
        podTemplateSpec.volumes.volumes == null
    }

    def "create a PodTempate with no init containers or containers"() {
        given:
        def kubePodTemplateSpec = Mock(V1PodTemplateSpec)
        def metadata = Mock(V1ObjectMeta)
        kubePodTemplateSpec.metadata >> metadata
        def spec = Mock(V1PodSpec)
        spec.serviceAccountName >> "serviceAcctName"
        List<V1Container> containers = new ArrayList<>()
        spec.containers >> containers
        kubePodTemplateSpec.spec >> spec

        when:
        def podTemplateSpec = new PodTemplate(kubePodTemplateSpec)

        then:
        podTemplateSpec.labels == null
        podTemplateSpec.annotations == null
        podTemplateSpec.serviceAccountName == "serviceAcctName"
        podTemplateSpec.initContainers == null
        podTemplateSpec.containers.size() == 0
        podTemplateSpec.volumes.volumes == null
    }
}
