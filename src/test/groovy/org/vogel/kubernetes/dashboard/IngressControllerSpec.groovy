package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class IngressControllerSpec extends Specification {
    def "test getting a list of Ingresses"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultIngress = Mock(Ingress)
        def resultList = [resultIngress]
        kubeUtil.getIngresses("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new IngressController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listIngresses(model, "default")

        then:
        result == "ingresses"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("ingresses", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of Ingresses with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getIngresses("default") >> { throw new ApiException() }
        def controller = new IngressController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listIngresses(model, "default")

        then:
        result == "error"
    }

    def "test describing a Ingress"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultIngress = Mock(Ingress)
        kubeUtil.getIngress("default", "my-ingress") >> resultIngress
        def controller = new IngressController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeIngress(model, "default", "my-ingress")

        then:
        result == "ingress_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("ingress", resultIngress)
        1 * model.addAttribute("ingressName", "my-ingress")
    }

    def "test describing a Ingress with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getIngress("default", "my-ingress") >> { throw new ApiException() }
        def controller = new IngressController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeIngress(model, "default", "my-ingress")

        then:
        result == "error"
    }
}
