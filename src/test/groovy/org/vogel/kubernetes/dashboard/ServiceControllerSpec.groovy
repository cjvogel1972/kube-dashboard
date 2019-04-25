package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class ServiceControllerSpec extends Specification {
    def "test getting a list of Services"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultService = Mock(Service)
        def resultList = [resultService]
        kubeUtil.getServices("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new ServiceController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listServices(model, "default")

        then:
        result == "services"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("services", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of Services with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getServices("default") >> { throw new ApiException() }
        def controller = new ServiceController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listServices(model, "default")

        then:
        result == "error"
    }

    def "test describing a Service"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultService = Mock(Service)
        kubeUtil.getService("default", "my-service") >> resultService
        def controller = new ServiceController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeService(model, "default", "my-service")

        then:
        result == "service_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("service", resultService)
        1 * model.addAttribute("serviceName", "my-service")
    }

    def "test describing a Service with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getService("default", "my-service") >> { throw new ApiException() }
        def controller = new ServiceController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeService(model, "default", "my-service")

        then:
        result == "error"
    }
}
