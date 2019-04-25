package org.vogel.kubernetes.dashboard.configmap

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import org.vogel.kubernetes.dashboard.KubernetesUtils
import spock.lang.Specification

class ConfigMapControllerSpec extends Specification {
    def "test getting a list of ConfigMaps"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultConfigMap = Mock(ConfigMap)
        def resultList = [resultConfigMap]
        kubeUtil.getConfigMaps("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new ConfigMapController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listConfigMaps(model, "default")

        then:
        result == "config_maps"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("configMaps", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of ConfigMaps with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getConfigMaps("default") >> { throw new ApiException() }
        def controller = new ConfigMapController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listConfigMaps(model, "default")

        then:
        result == "error"
    }

    def "test describing a ConfigMap"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultConfigMap = Mock(ConfigMap)
        kubeUtil.getConfigMap("default", "my-config-map") >> resultConfigMap
        def controller = new ConfigMapController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeConfigMap(model, "default", "my-config-map")

        then:
        result == "config_map_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("configMap", resultConfigMap)
        1 * model.addAttribute("configMapName", "my-config-map")
    }

    def "test describing a ConfigMap with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getConfigMap("default", "my-config-map") >> { throw new ApiException() }
        def controller = new ConfigMapController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describeConfigMap(model, "default", "my-config-map")

        then:
        result == "error"
    }
}
