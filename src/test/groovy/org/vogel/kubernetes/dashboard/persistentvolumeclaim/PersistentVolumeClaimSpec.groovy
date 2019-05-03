package org.vogel.kubernetes.dashboard.persistentvolumeclaim

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.models.*
import org.joda.time.DateTime
import spock.lang.Specification

class PersistentVolumeClaimSpec extends Specification {
    def "creating a PersistentVolumeClaim mainly empty"() {
        given:
        def kubePersistentVolumeClaim = Mock(V1PersistentVolumeClaim)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolumeClaim.metadata >> metadata
        def spec = Mock(V1PersistentVolumeClaimSpec)
        kubePersistentVolumeClaim.spec >> spec
        def status = Mock(V1PersistentVolumeClaimStatus)
        kubePersistentVolumeClaim.status >> status

        when:
        def pvc = new PersistentVolumeClaim(kubePersistentVolumeClaim)

        then:
        pvc.status == null
        pvc.volume == null
        pvc.capacity == ""
        pvc.accessModes == ""
        pvc.storageClass == ""
        pvc.deletionTimestamp == "0s"
        pvc.finalizers == "[]"
        pvc.volumeMode == null
        pvc.conditions == null
    }

    def "creating a PersistentVolumeClaim"() {
        given:
        def kubePersistentVolumeClaim = Mock(V1PersistentVolumeClaim)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> ['volume.beta.kubernetes.io/storage-class': 'foo']
        def deletionTimestamp = DateTime.now()
        metadata.deletionTimestamp >> deletionTimestamp
        metadata.finalizers >> ["foo", "bar"]
        kubePersistentVolumeClaim.metadata >> metadata
        def spec = Mock(V1PersistentVolumeClaimSpec)
        spec.accessModes >> ["ReadWriteMany"]
        spec.volumeName >> "mount"
        kubePersistentVolumeClaim.spec >> spec
        def status = Mock(V1PersistentVolumeClaimStatus)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        status.capacity >> capacity
        def claimCondition = Mock(V1PersistentVolumeClaimCondition)
        status.conditions >> [claimCondition]
        kubePersistentVolumeClaim.status >> status

        when:
        def pvc = new PersistentVolumeClaim(kubePersistentVolumeClaim)

        then:
        pvc.status == "Terminating"
        pvc.volume == "mount"
        pvc.capacity == "1"
        pvc.accessModes == "RWX"
        pvc.storageClass == "foo"
        pvc.deletionTimestamp == "0s"
        pvc.finalizers == "[foo, bar]"
        pvc.volumeMode == null
        pvc.conditions.size() == 1
    }

    def "creating a PersistentVolumeClaim storage class name"() {
        given:
        def kubePersistentVolumeClaim = Mock(V1PersistentVolumeClaim)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolumeClaim.metadata >> metadata
        def spec = Mock(V1PersistentVolumeClaimSpec)
        spec.storageClassName >> "storage"
        kubePersistentVolumeClaim.spec >> spec
        def status = Mock(V1PersistentVolumeClaimStatus)
        kubePersistentVolumeClaim.status >> status

        when:
        def pvc = new PersistentVolumeClaim(kubePersistentVolumeClaim)

        then:
        pvc.status == null
        pvc.volume == null
        pvc.capacity == ""
        pvc.accessModes == ""
        pvc.storageClass == "storage"
        pvc.deletionTimestamp == "0s"
        pvc.finalizers == "[]"
        pvc.volumeMode == null
        pvc.conditions == null
    }
}
