package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1ObjectMeta
import org.joda.time.DateTime
import spock.lang.Specification

class MetadataSpec extends Specification {

    def "create a Metadata object with all data"() {
        setup:
        def kubeMetatdata = Mock(V1ObjectMeta)
        kubeMetatdata.name >> "foo"
        kubeMetatdata.namespace >> "bar"
        kubeMetatdata.uid >> "01234567890abcdef"
        def creationTimestamp = DateTime.now().minusDays(1)
        kubeMetatdata.creationTimestamp >> creationTimestamp
        kubeMetatdata.labels >> [app: 'app-name', foo: 'bar']
        kubeMetatdata.annotations >> [bar: 'foo']

        when:
        def metadata = new Metadata(kubeMetatdata)

        then:
        metadata.name == "foo"
        metadata.namespace == "bar"
        metadata.uid == "01234567890abcdef"
        metadata.creationTimestamp == creationTimestamp
        metadata.age == "1d"
        metadata.labels == ['app=app-name', 'foo=bar']
        metadata.annotations == ['bar=foo']
    }

    def "create a Metadata object without labels or annotations"() {
        setup:
        def kubeMetatdata = Mock(V1ObjectMeta)
        kubeMetatdata.name >> "foo"
        kubeMetatdata.namespace >> "bar"
        kubeMetatdata.uid >> "01234567890abcdef"
        def creationTimestamp = DateTime.now().minusDays(1)
        kubeMetatdata.creationTimestamp >> creationTimestamp

        when:
        def metadata = new Metadata(kubeMetatdata)

        then:
        metadata.name == "foo"
        metadata.namespace == "bar"
        metadata.uid == "01234567890abcdef"
        metadata.creationTimestamp == creationTimestamp
        metadata.age == "1d"
        metadata.labels == null
        metadata.annotations == null
    }
}
