package org.vogel.kubernetes.dashboard.service

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.models.*
import spock.lang.Specification

class ServiceSpec extends Specification {
    def "test creating a Service mainly empty"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "foo"
        serviceSpec.ports >> []
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "foo"
        service.clusterIp == "<none>"
        service.externalIp == "<unknown>"
        service.ports == "<none>"
        service.selector == null
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service mainly empty ports null"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "foo"
        serviceSpec.ports >> null
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "foo"
        service.clusterIp == "<none>"
        service.externalIp == "<unknown>"
        service.ports == "<none>"
        service.selector == null
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service NodePort"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "NodePort"
        def port = Mock(V1ServicePort)
        port.nodePort >> 31000
        port.port >> 80
        port.protocol >> "TCP"
        serviceSpec.ports >> [port]
        serviceSpec.selector >> ["app": "kube-dashboard"]
        serviceSpec.externalIPs >> ["127.0.0.1", "192.168.0.1"]
        serviceSpec.sessionAffinity >> "None"
        serviceSpec.externalTrafficPolicy >> "Cluster"
        serviceSpec.healthCheckNodePort >> 8181
        serviceSpec.loadBalancerSourceRanges >> ["172.0.0.1", "172.0.0.2"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "NodePort"
        service.clusterIp == "<none>"
        service.externalIp == "127.0.0.1,192.168.0.1"
        service.ports == "80:31000/TCP"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == "127.0.0.1,192.168.0.1"
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == "None"
        service.externalTrafficPolicy == "Cluster"
        service.healthCheckNodePort == 8181
        service.loadBalancerSourceRanges == "172.0.0.1,172.0.0.2"
    }

    def "test creating a Service NodePort no NodePort on port"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "NodePort"
        def port = Mock(V1ServicePort)
        port.port >> 80
        port.protocol >> "TCP"
        serviceSpec.ports >> [port]
        serviceSpec.selector >> ["app": "kube-dashboard"]
        serviceSpec.externalIPs >> ["127.0.0.1", "192.168.0.1"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "NodePort"
        service.clusterIp == "<none>"
        service.externalIp == "127.0.0.1,192.168.0.1"
        service.ports == "80/TCP"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == "127.0.0.1,192.168.0.1"
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service NodePort zero NodePort on port"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "NodePort"
        def port = Mock(V1ServicePort)
        port.nodePort >> 0
        port.port >> 80
        port.protocol >> "TCP"
        serviceSpec.ports >> [port]
        serviceSpec.selector >> ["app": "kube-dashboard"]
        serviceSpec.externalIPs >> ["127.0.0.1", "192.168.0.1"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "NodePort"
        service.clusterIp == "<none>"
        service.externalIp == "127.0.0.1,192.168.0.1"
        service.ports == "80/TCP"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == "127.0.0.1,192.168.0.1"
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service ClusterIP no external IP"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "ClusterIP"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "ClusterIP"
        service.clusterIp == "<none>"
        service.externalIp == "<none>"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service LoadBalancer"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "LoadBalancer"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        serviceSpec.externalIPs >> ["127.0.0.1", "192.168.0.1"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        def ingress = Mock(V1LoadBalancerIngress)
        ingress.ip >> "172.0.0.1"
        loadBalancerStatus.ingress >> [ingress]
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "LoadBalancer"
        service.clusterIp == "<none>"
        service.externalIp == "172.0.0.1,127.0.0.1,192.168.0.1"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == "127.0.0.1,192.168.0.1"
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == "172.0.0.1"
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service LoadBalancer no external IPs ingress IP"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "LoadBalancer"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        def ingress = Mock(V1LoadBalancerIngress)
        ingress.ip >> "172.0.0.1"
        loadBalancerStatus.ingress >> [ingress]
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "LoadBalancer"
        service.clusterIp == "<none>"
        service.externalIp == "172.0.0.1"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == "172.0.0.1"
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service LoadBalancer no external IPs ingress hostname"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "LoadBalancer"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        def ingress = Mock(V1LoadBalancerIngress)
        ingress.hostname >> "foo.server.com"
        loadBalancerStatus.ingress >> [ingress]
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "LoadBalancer"
        service.clusterIp == "<none>"
        service.externalIp == "foo.server.com"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == "foo.server.com"
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service LoadBalancer no external IPs ingress blank"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "LoadBalancer"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        def ingress = Mock(V1LoadBalancerIngress)
        loadBalancerStatus.ingress >> [ingress]
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        println "*${service.externalIp}*"
        service.svcType == "LoadBalancer"
        service.clusterIp == "<none>"
        service.externalIp == "null"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == "null"
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service LoadBalancer no external IPs no ingress"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "LoadBalancer"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "LoadBalancer"
        service.clusterIp == "<none>"
        service.externalIp == "<pending>"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service ExternalName"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "ExternalName"
        serviceSpec.ports >> []
        serviceSpec.selector >> ["app": "kube-dashboard"]
        serviceSpec.externalIPs >> ["127.0.0.1", "192.168.0.1"]
        serviceSpec.externalName >> "foo.domain.com"
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus

        when:
        def service = new Service(kubeService)

        then:
        service.svcType == "ExternalName"
        service.clusterIp == "<none>"
        service.externalIp == "foo.domain.com"
        service.ports == "<none>"
        service.selector == "app=kube-dashboard"
        service.specExternalIp == "127.0.0.1,192.168.0.1"
        service.loadBalancerIp == null
        service.externalName == "foo.domain.com"
        service.loadBalancerIngress == null
        service.servicePorts == null
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }

    def "test creating a Service mainly empty with Endpoint list"() {
        given:
        def kubeService = Mock(V1Service)
        def metadata = Mock(V1ObjectMeta)
        kubeService.metadata >> metadata
        def serviceSpec = Mock(V1ServiceSpec)
        serviceSpec.type >> "foo"
        def kubeServicePort = Mock(V1ServicePort)
        kubeServicePort.port >> 8080
        kubeServicePort.protocol >> 'http'
        kubeServicePort.targetPort >> new IntOrString(8080)
        kubeServicePort.nodePort >> 30001
        serviceSpec.ports >> [kubeServicePort]
        kubeService.spec >> serviceSpec
        def serviceStatus = Mock(V1ServiceStatus)
        def loadBalancerStatus = Mock(V1LoadBalancerStatus)
        serviceStatus.loadBalancer >> loadBalancerStatus
        kubeService.status >> serviceStatus
        def kubeEndpoint = Mock(V1Endpoints)
        def subsets = []
        kubeEndpoint.subsets >> subsets

        when:
        def service = new Service(kubeService, kubeEndpoint)

        then:
        service.svcType == "foo"
        service.clusterIp == "<none>"
        service.externalIp == "<unknown>"
        service.ports == "8080:30001/http"
        service.selector == null
        service.specExternalIp == null
        service.loadBalancerIp == null
        service.externalName == null
        service.loadBalancerIngress == null
        def servicePort = service.servicePorts[0]
        servicePort.port == '<unset> 8080/http'
        servicePort.endpoints == '<none>'
        servicePort.targetPort == '8080/http'
        servicePort.nodePort == '<unset> 30001/http'
        service.sessionAffinity == null
        service.externalTrafficPolicy == null
        service.healthCheckNodePort == 0
        service.loadBalancerSourceRanges == null
    }
}
