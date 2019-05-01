package org.vogel.kubernetes.dashboard

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.models.*
import spock.lang.Specification

class IngressRuleSpec extends Specification {
    def "create an IngressRule pretty empty"() {
        given:
        V1beta1IngressRule kubeIngRule = new V1beta1IngressRule()
        V1beta1HTTPIngressRuleValue httpIngRule = new V1beta1HTTPIngressRuleValue()
        kubeIngRule.setHttp(httpIngRule)
        KubernetesUtils kubeUtils = Mock(KubernetesUtils)

        when:
        def rule = new IngressRule(kubeIngRule, "default", kubeUtils)

        then:
        rule.host == "*"
        rule.paths.isEmpty()
        rule.backends.isEmpty()
    }

    def "create an IngressRule"() {
        given:
        V1beta1IngressRule kubeIngRule = new V1beta1IngressRule()
        kubeIngRule.setHost("www.abc.com")
        List<V1beta1HTTPIngressPath> ingressPaths = new ArrayList<>()
        V1beta1HTTPIngressPath path = new V1beta1HTTPIngressPath()
        path.setPath("/foo")
        def backend = new V1beta1IngressBackend()
        backend.setServiceName("bar")
        backend.setServicePort(new IntOrString(8080))
        path.setBackend(backend)
        path.setBackend(backend)
        ingressPaths.add(path)
        V1beta1HTTPIngressRuleValue httpIngRule = new V1beta1HTTPIngressRuleValue()
        httpIngRule.setPaths(ingressPaths)
        kubeIngRule.setHttp(httpIngRule)
        KubernetesUtils kubeUtils = Mock(KubernetesUtils)
        V1EndpointsList endpointsList = Mock(V1EndpointsList)
        List<V1Endpoints> endpoints = new ArrayList<>()
        endpointsList.getItems() >> endpoints
        kubeUtils.getEndpoint(_, _) >> endpointsList
        V1Service service = Mock(V1Service)
        V1ServiceSpec serviceSpec = Mock(V1ServiceSpec)
        List<V1ServicePort> ports = new ArrayList<>()
        serviceSpec.ports >> ports
        service.spec >> serviceSpec
        kubeUtils.getKubeService(_, _) >> service

        when:
        def rule = new IngressRule(kubeIngRule, "default", kubeUtils)

        then:
        rule.host == "www.abc.com"
        rule.paths.size() == 1
        rule.paths[0] == "/foo"
        rule.backends.size() == 1
        rule.backends[0] == "bar:8080 (<none>)"
    }

    def "create an IngressRule port is string"() {
        given:
        V1beta1IngressRule kubeIngRule = new V1beta1IngressRule()
        kubeIngRule.setHost("www.abc.com")
        List<V1beta1HTTPIngressPath> ingressPaths = new ArrayList<>()
        V1beta1HTTPIngressPath path = new V1beta1HTTPIngressPath()
        path.setPath("/foo")
        def backend = new V1beta1IngressBackend()
        backend.setServiceName("bar")
        backend.setServicePort(new IntOrString("8080"))
        path.setBackend(backend)
        path.setBackend(backend)
        ingressPaths.add(path)
        V1beta1HTTPIngressRuleValue httpIngRule = new V1beta1HTTPIngressRuleValue()
        httpIngRule.setPaths(ingressPaths)
        kubeIngRule.setHttp(httpIngRule)
        KubernetesUtils kubeUtils = Mock(KubernetesUtils)
        V1EndpointsList endpointsList = Mock(V1EndpointsList)
        List<V1Endpoints> endpoints = new ArrayList<>()
        endpointsList.getItems() >> endpoints
        kubeUtils.getEndpoint(_, _) >> endpointsList
        V1Service service = Mock(V1Service)
        V1ServiceSpec serviceSpec = Mock(V1ServiceSpec)
        List<V1ServicePort> ports = new ArrayList<>()
        serviceSpec.ports >> ports
        service.spec >> serviceSpec
        kubeUtils.getKubeService(_, _) >> service

        when:
        def rule = new IngressRule(kubeIngRule, "default", kubeUtils)

        then:
        rule.host == "www.abc.com"
        rule.paths.size() == 1
        rule.paths[0] == "/foo"
        rule.backends.size() == 1
        rule.backends[0] == "bar:8080 (<none>)"
    }
}
