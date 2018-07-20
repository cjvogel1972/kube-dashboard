package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1ConfigMap
import io.kubernetes.client.models.V1ObjectMeta
import spock.lang.Specification

class ConfigMapSpec extends Specification {
    def "test creating a ConfigMap"() {
        given:
        def kubeConfigMap = Mock(V1ConfigMap)
        def metadata = Mock(V1ObjectMeta)
        kubeConfigMap.metadata >> metadata
        def data = [foo: 'bar', bar: 'foo']
        kubeConfigMap.data >> data

        when:
        def configMap = new ConfigMap(kubeConfigMap)

        then:
        configMap.data == data
        configMap.dataSize == 2
    }

    def "test creating a ConfigMap with no data"() {
        given:
        def kubeConfigMap = Mock(V1ConfigMap)
        def metadata = Mock(V1ObjectMeta)
        kubeConfigMap.metadata >> metadata

        when:
        def configMap = new ConfigMap(kubeConfigMap)

        then:
        configMap.data == [:]
        configMap.dataSize == 0
    }
}
