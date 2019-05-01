package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class DeploymentControllerSpec extends Specification {
    def "test getting a list of Deployments"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultDeployment = Mock(Deployment)
        def resultList = [resultDeployment]
        kubeUtil.getDeployments("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new DeploymentController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listDeployments(model, "default")

        then:
        result == "deployments"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("deployments", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of Deployments with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getDeployments("default") >> { throw new ApiException() }
        def controller = new DeploymentController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listDeployments(model, "default")

        then:
        result == "error"
    }

    def "test describing a Deployment"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultDeployment = Mock(Deployment)
        kubeUtil.getDeployment("default", "my-deployment") >> resultDeployment
        def controller = new DeploymentController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeDeployment(model, "default", "my-deployment")

        then:
        result == "deployment_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("deployment", resultDeployment)
        1 * model.addAttribute("deploymentName", "my-deployment")
    }

    def "test describing a Deployment with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getDeployment("default", "my-deployment") >> { throw new ApiException() }
        def controller = new DeploymentController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeDeployment(model, "default", "my-deployment")

        then:
        result == "error"
    }
}
