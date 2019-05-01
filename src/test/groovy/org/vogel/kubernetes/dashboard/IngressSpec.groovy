package org.vogel.kubernetes.dashboard

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.models.*
import spock.lang.Specification

class IngressSpec extends Specification {
    def "create an Ingress pretty empty"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        spec.rules >> ingressRules
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "*"
        ingress.addresses == null
        ingress.ports == "80"
        ingress.defaultBackend == null
        ingress.tls == null
        ingress.rules == null
    }

    def "create an Ingress pretty empty with TLS"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        spec.rules >> ingressRules
        List<V1beta1IngressTLS> specTls = new ArrayList<>()
        V1beta1IngressTLS tls = new V1beta1IngressTLS()
        List<String> hosts = new ArrayList<>()
        hosts.add("www.abc.com")
        tls.setHosts(hosts)
        specTls.add(tls)
        spec.tls >> specTls
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "*"
        ingress.addresses == null
        ingress.ports == "80, 443"
        ingress.defaultBackend == null
        ingress.tls.size() == 1
        ingress.tls[0] == "SNI routes www.abc.com"
        ingress.rules == null
    }

    def "create an Ingress pretty empty with TLS with secret name"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        spec.rules >> ingressRules
        List<V1beta1IngressTLS> specTls = new ArrayList<>()
        V1beta1IngressTLS tls = new V1beta1IngressTLS()
        List<String> hosts = new ArrayList<>()
        hosts.add("www.abc.com")
        tls.setHosts(hosts)
        tls.setSecretName("foosecret")
        specTls.add(tls)
        spec.tls >> specTls
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "*"
        ingress.addresses == null
        ingress.ports == "80, 443"
        ingress.defaultBackend == null
        ingress.tls.size() == 1
        ingress.tls[0] == "foosecret terminates www.abc.com"
        ingress.rules == null
    }

    def "create an Ingress with hosts and ingress IP"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        V1beta1IngressRule rule = new V1beta1IngressRule()
        rule.setHost("www.abc.com")
        ingressRules.add(rule)
        spec.rules >> ingressRules
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        List<V1LoadBalancerIngress> ingressList = new ArrayList<>()
        V1LoadBalancerIngress ing = new V1LoadBalancerIngress()
        ing.setIp("172.0.0.1")
        ingressList.add(ing)
        loadBalancer.ingress >> ingressList
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "www.abc.com"
        ingress.addresses == "172.0.0.1"
        ingress.ports == "80"
        ingress.defaultBackend == null
        ingress.tls == null
        ingress.rules == null
    }

    def "create an Ingress with hosts and ingress host name"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        V1beta1IngressRule rule = new V1beta1IngressRule()
        rule.setHost("www.abc.com")
        ingressRules.add(rule)
        spec.rules >> ingressRules
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        List<V1LoadBalancerIngress> ingressList = new ArrayList<>()
        V1LoadBalancerIngress ing = new V1LoadBalancerIngress()
        ing.setHostname("www.xyz.com")
        ingressList.add(ing)
        loadBalancer.ingress >> ingressList
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "www.abc.com"
        ingress.addresses == "www.xyz.com"
        ingress.ports == "80"
        ingress.defaultBackend == null
        ingress.tls == null
        ingress.rules == null
    }

    def "create an Ingress with hosts and ingress empty"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        V1beta1IngressRule rule = new V1beta1IngressRule()
        rule.setHost("www.abc.com")
        ingressRules.add(rule)
        spec.rules >> ingressRules
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        List<V1LoadBalancerIngress> ingressList = new ArrayList<>()
        V1LoadBalancerIngress ing = new V1LoadBalancerIngress()
        ingressList.add(ing)
        loadBalancer.ingress >> ingressList
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status

        when:
        def ingress = new Ingress(kubeIng)

        then:
        ingress.hosts == "www.abc.com"
        ingress.addresses == "null"
        ingress.ports == "80"
        ingress.defaultBackend == null
        ingress.tls == null
        ingress.rules == null
    }

    def "create an Ingress with KubeUtil"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        spec.rules >> ingressRules
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status
        def kubeUtils = Mock(KubernetesUtils)
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
        def ingress = new Ingress(kubeIng, kubeUtils)

        then:
        ingress.hosts == "*"
        ingress.addresses == null
        ingress.ports == "80"
        ingress.defaultBackend == "default-http-backend:80 (<none>)"
        ingress.tls == null
        ingress.rules.size() == 0
    }

    def "create an Ingress with KubeUtil with backend"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        V1beta1IngressRule rule1 = new V1beta1IngressRule()
        ingressRules.add(rule1)
        V1beta1IngressRule rule2 = new V1beta1IngressRule()
        V1beta1HTTPIngressRuleValue httpIngRule = new V1beta1HTTPIngressRuleValue()
        rule2.setHttp(httpIngRule)
        rule2.setHost("www.abc.com")
        ingressRules.add(rule2)
        spec.rules >> ingressRules
        V1beta1IngressBackend backend = new V1beta1IngressBackend()
        backend.setServiceName("foo")
        backend.setServicePort(new IntOrString("8000"))
        spec.backend >> backend
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status
        def kubeUtils = Mock(KubernetesUtils)
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
        def ingress = new Ingress(kubeIng, kubeUtils)

        then:
        ingress.hosts == "www.abc.com"
        ingress.addresses == null
        ingress.ports == "80"
        ingress.defaultBackend == "foo:8000 (<none>)"
        ingress.tls == null
        ingress.rules.size() == 1
        ingress.rules[0].host == "www.abc.com"
        ingress.rules[0].paths.size() == 0
        ingress.rules[0].backends.size() == 0

    }

    def "create an Ingress with KubeUtil with backend port is int"() {
        given:
        V1beta1Ingress kubeIng = Mock(V1beta1Ingress)
        def metadata = Mock(V1ObjectMeta)
        kubeIng.metadata >> metadata
        def spec = Mock(V1beta1IngressSpec)
        List<V1beta1IngressRule> ingressRules = new ArrayList<>()
        spec.rules >> ingressRules
        V1beta1IngressBackend backend = new V1beta1IngressBackend()
        backend.setServiceName("foo")
        backend.setServicePort(new IntOrString(8000))
        spec.backend >> backend
        kubeIng.spec >> spec
        def status = Mock(V1beta1IngressStatus)
        V1LoadBalancerStatus loadBalancer = Mock(V1LoadBalancerStatus)
        status.loadBalancer >> loadBalancer
        kubeIng.status >> status
        def kubeUtils = Mock(KubernetesUtils)
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
        def ingress = new Ingress(kubeIng, kubeUtils)

        then:
        ingress.hosts == "*"
        ingress.addresses == null
        ingress.ports == "80"
        ingress.defaultBackend == "foo:8000 (<none>)"
        ingress.tls == null
        ingress.rules.size() == 0
    }
}
