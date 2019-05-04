package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class PodControllerSpec extends Specification {
    def "test getting a list of Pods"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPod = Mock(Pod)
        def resultList = [resultPod]
        kubeUtil.getPods("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPods(model, "default")

        then:
        result == "pods"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("pods", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of Pods with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPods("default") >> { throw new ApiException() }
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPods(model, "default")

        then:
        result == "error"
    }

    def "test describing a Pod"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPod = Mock(Pod)
        kubeUtil.getPod("default", "my-pod") >> resultPod
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePod(model, "default", "my-pod")

        then:
        result == "pod_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("pod", resultPod)
        1 * model.addAttribute("podName", "my-pod")
    }

    def "test describing a Pod with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPod("default", "my-pod") >> { throw new ApiException() }
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePod(model, "default", "my-pod")

        then:
        result == "error"
    }

    def "test show a Pod log"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPod = "foo\n\tbar\n"
        kubeUtil.getPodLogs("default", "my-pod") >> resultPod
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.showPodLogs(model, "default", "my-pod")

        then:
        result == "logs"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("logs", ["foo", "        bar"])
        1 * model.addAttribute("podName", "my-pod")
    }

    def "test show a Pod log with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPodLogs("default", "my-pod") >> { throw new ApiException() }
        def controller = new PodController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.showPodLogs(model, "default", "my-pod")

        then:
        result == "error"
    }
}
