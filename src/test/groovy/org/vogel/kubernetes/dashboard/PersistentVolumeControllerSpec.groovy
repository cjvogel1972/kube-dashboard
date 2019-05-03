package org.vogel.kubernetes.dashboard

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import spock.lang.Specification

class PersistentVolumeControllerSpec extends Specification {
    def "test getting a list of PersistentVolumes"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPersistentVolume = Mock(PersistentVolume)
        def resultList = [resultPersistentVolume]
        kubeUtil.getPersistentVolumes() >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new PersistentVolumeController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPersistentVolumes(model, "default")

        then:
        result == "persistent_volumes"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("persistentVolumes", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of PersistentVolumes with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPersistentVolumes() >> { throw new ApiException() }
        def controller = new PersistentVolumeController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPersistentVolumes(model, "default")

        then:
        result == "error"
    }

    def "test describing a PersistentVolume"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPersistentVolume = Mock(PersistentVolume)
        kubeUtil.getPersistentVolume("my-persistent-volume") >> resultPersistentVolume
        def controller = new PersistentVolumeController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePersistentVolume(model, "default", "my-persistent-volume")

        then:
        result == "persistent_volume_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("persistentVolume", resultPersistentVolume)
        1 * model.addAttribute("persistentVolumeName", "my-persistent-volume")
    }

    def "test describing a PersistentVolume with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPersistentVolume("my-persistent-volume") >> { throw new ApiException() }
        def controller = new PersistentVolumeController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePersistentVolume(model, "default", "my-persistent-volume")

        then:
        result == "error"
    }
}