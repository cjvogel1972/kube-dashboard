package org.vogel.kubernetes.dashboard.service

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.models.V1Endpoints
import io.kubernetes.client.models.V1ServicePort
import spock.lang.Specification

class ServicePortSpec extends Specification {
    def "create a ServicePort"() {
        given:
        def kubeServicePort = Mock(V1ServicePort)
        kubeServicePort.port >> 8080
        kubeServicePort.protocol >> 'http'
        kubeServicePort.targetPort >> new IntOrString(8080)
        kubeServicePort.nodePort >> 30001
        def kubeEndpoint = Mock(V1Endpoints)
        def subsets = []
        kubeEndpoint.subsets >> subsets

        when:
        def servicePort = new ServicePort(kubeServicePort, kubeEndpoint)

        then:
        servicePort.port == '<unset> 8080/http'
        servicePort.endpoints == '<none>'
        servicePort.targetPort == '8080/http'
        servicePort.nodePort == '<unset> 30001/http'
    }

    def "create a ServicePort target port is string"() {
        given:
        def kubeServicePort = Mock(V1ServicePort)
        kubeServicePort.port >> 8080
        kubeServicePort.protocol >> 'http'
        kubeServicePort.targetPort >> new IntOrString("8080")
        kubeServicePort.nodePort >> 30001
        def kubeEndpoint = Mock(V1Endpoints)
        def subsets = []
        kubeEndpoint.subsets >> subsets

        when:
        def servicePort = new ServicePort(kubeServicePort, kubeEndpoint)

        then:
        servicePort.port == '<unset> 8080/http'
        servicePort.endpoints == '<none>'
        servicePort.targetPort == '8080/http'
        servicePort.nodePort == '<unset> 30001/http'
    }

    def "create a ServicePort no node port"() {
        given:
        def kubeServicePort = Mock(V1ServicePort)
        kubeServicePort.port >> 8080
        kubeServicePort.protocol >> 'http'
        kubeServicePort.targetPort >> new IntOrString(8080)
        def kubeEndpoint = Mock(V1Endpoints)
        def subsets = []
        kubeEndpoint.subsets >> subsets

        when:
        def servicePort = new ServicePort(kubeServicePort, kubeEndpoint)

        then:
        servicePort.port == '<unset> 8080/http'
        servicePort.endpoints == '<none>'
        servicePort.targetPort == '8080/http'
        servicePort.nodePort == null
    }

    def "create a ServicePort node port is zero"() {
        given:
        def kubeServicePort = Mock(V1ServicePort)
        kubeServicePort.port >> 8080
        kubeServicePort.protocol >> 'http'
        kubeServicePort.targetPort >> new IntOrString(8080)
        kubeServicePort.nodePort >> 0
        def kubeEndpoint = Mock(V1Endpoints)
        def subsets = []
        kubeEndpoint.subsets >> subsets

        when:
        def servicePort = new ServicePort(kubeServicePort, kubeEndpoint)

        then:
        servicePort.port == '<unset> 8080/http'
        servicePort.endpoints == '<none>'
        servicePort.targetPort == '8080/http'
        servicePort.nodePort == null
    }
}
