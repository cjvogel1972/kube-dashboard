package org.vogel.kubernetes.dashboard.persistentvolumeclaim

import io.kubernetes.client.ApiException
import org.springframework.ui.Model
import org.vogel.kubernetes.dashboard.KubernetesUtils
import spock.lang.Specification

class PersistentVolumeClaimControllerSpec extends Specification {
    def "test getting a list of PersistentVolumeClaims"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPersistentVolumeClaim = Mock(PersistentVolumeClaim)
        def resultList = [resultPersistentVolumeClaim]
        kubeUtil.getPersistentVolumeClaims("default") >> resultList
        def namespaces = ["default", "kube-system"]
        kubeUtil.getNamespaces() >> namespaces
        def controller = new PersistentVolumeClaimController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPersistentVolumeClaims(model, "default")

        then:
        result == "persistent_volume_claims"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("persistentVolumeClaims", resultList)
        1 * model.addAttribute("namespaces", namespaces)
    }

    def "test getting a list of PersistentVolumeClaims with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPersistentVolumeClaims("default") >> { throw new ApiException() }
        def controller = new PersistentVolumeClaimController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.listPersistentVolumeClaims(model, "default")

        then:
        result == "error"
    }

    def "test describing a PersistentVolumeClaim"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        def resultPersistentVolumeClaim = Mock(PersistentVolumeClaim)
        kubeUtil.getPersistentVolumeClaim("default", "my-persistent-volume-claim") >> resultPersistentVolumeClaim
        def controller = new PersistentVolumeClaimController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePersistentVolumeClaim(model, "default", "my-persistent-volume-claim")

        then:
        result == "persistent_volume_claim_describe"
        1 * model.addAttribute("namespace", "default")
        1 * model.addAttribute("persistentVolumeClaim", resultPersistentVolumeClaim)
        1 * model.addAttribute("persistentVolumeClaimName", "my-persistent-volume-claim")
    }

    def "test describing a PersistentVolumeClaim with exception"() {
        given:
        def kubeUtil = Mock(KubernetesUtils)
        kubeUtil.getPersistentVolumeClaim("default", "my-persistent-volume-claim") >> { throw new ApiException() }
        def controller = new PersistentVolumeClaimController(kubeUtil)
        def model = Mock(Model)

        when:
        def result = controller.describePersistentVolumeClaim(model, "default", "my-persistent-volume-claim")

        then:
        result == "error"
    }
}