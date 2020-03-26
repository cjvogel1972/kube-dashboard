package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class DashboardControllerSpec extends Specification {
    def "test getting the index page"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new DashboardController(kubeUtil, "default")
        def model = Mock(Model)

        when:
        def result = controller.index(model)

        then:
        result == "index"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting the index page for a given namespace"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new DashboardController(kubeUtil, "default")
        def model = Mock(Model)

        when:
        def result = controller.namespace(model, "kube-system")

        then:
        result == "index"
        1 * model.addAttribute("namespace", "kube-system")
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting the index page for a given namespace with an exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getNamespaces() >> { throw new ApiException() }
        def controller = new DashboardController(kubeUtil, "default")
        def model = Mock(Model)

        when:
        def result = controller.namespace(model, "kube-system")

        then:
        result == "error"
    }
}
