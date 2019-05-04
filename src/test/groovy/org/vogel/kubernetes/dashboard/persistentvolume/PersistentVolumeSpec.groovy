package org.vogel.kubernetes.dashboard.persistentvolume

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.models.*
import org.joda.time.DateTime
import spock.lang.Specification

class PersistentVolumeSpec extends Specification {
    def "creating a PersistentVolume mainly empty"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source == null
    }

    def "creating a PersistentVolume"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> ['volume.beta.kubernetes.io/storage-class': 'foo']
        def deletionTimestamp = DateTime.now()
        metadata.deletionTimestamp >> deletionTimestamp
        metadata.finalizers >> ["foo", "bar"]
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def claimRef = Mock(V1ObjectReference)
        claimRef.namespace >> "default"
        claimRef.name >> "claim"
        spec.claimRef >> claimRef
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == "Terminating"
        pv.claim == "default/claim"
        pv.storageClass == "foo"
        pv.reason == null
        pv.finalizers == "[foo, bar]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source == null
    }

    def "creating a PersistentVolume host path"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1HostPathVolumeSource)
        spec.hostPath >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 3
    }

    def "creating a PersistentVolume GCE disk"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1GCEPersistentDiskVolumeSource)
        volumeSource.partition >> 1
        spec.gcePersistentDisk >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 5
    }

    def "creating a PersistentVolume AWS elastic block"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1AWSElasticBlockStoreVolumeSource)
        volumeSource.partition >> 1
        spec.awsElasticBlockStore >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 5
    }

    def "creating a PersistentVolume NFS"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1NFSVolumeSource)
        spec.nfs >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 4
    }

    def "creating a PersistentVolume iSCSI"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1ISCSIPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.lun >> 1
        volumeSource.portals >> []
        spec.iscsi >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 12
    }

    def "creating a PersistentVolume Glusterfs"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1GlusterfsVolumeSource)
        spec.glusterfs >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 4
    }

    def "creating a PersistentVolume RBD"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1RBDPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.monitors >> []
        spec.rbd >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 9
    }

    def "creating a PersistentVolume Quobyte"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1QuobyteVolumeSource)
        spec.quobyte >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 4
    }

    def "creating a PersistentVolume Azure disk"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1AzureDiskVolumeSource)
        spec.azureDisk >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 7
    }

    def "creating a PersistentVolume vSphere"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1VsphereVirtualDiskVolumeSource)
        spec.vsphereVolume >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 4
    }

    def "creating a PersistentVolume Cinder"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1CinderPersistentVolumeSource)
        spec.cinder >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 4
    }

    def "creating a PersistentVolume Photon"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1PhotonPersistentDiskVolumeSource)
        spec.photonPersistentDisk >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 3
    }

    def "creating a PersistentVolume Portworx"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1PortworxVolumeSource)
        spec.portworxVolume >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 2
    }

    def "creating a PersistentVolume ScaleIO"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1ScaleIOPersistentVolumeSource)
        spec.scaleIO >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 11
    }

    def "creating a PersistentVolume Cephfs"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1CephFSPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.monitors >> []
        spec.cephfs >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 7
    }

    def "creating a PersistentVolume StorageOS"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1StorageOSPersistentVolumeSource)
        spec.storageos >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 5
    }

    def "creating a PersistentVolume FC"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1FCVolumeSource)
        volumeSource.targetWWNs >> []
        spec.fc >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 5
    }

    def "creating a PersistentVolume Azure file"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1AzureFilePersistentVolumeSource)
        spec.azureFile >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 5
    }

    def "creating a PersistentVolume Flex"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1FlexPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.options >> [:]
        spec.flexVolume >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 6
    }

    def "creating a PersistentVolume Flocker"() {
        given:
        def kubePersistentVolume = Mock(V1PersistentVolume)
        def metadata = Mock(V1ObjectMeta)
        metadata.annotations >> [bar: 'foo']
        kubePersistentVolume.metadata >> metadata
        def spec = Mock(V1PersistentVolumeSpec)
        Map<String, Quantity> capacity = new HashMap<>()
        Quantity storage = new Quantity(new BigDecimal(1), Quantity.Format.DECIMAL_EXPONENT)
        capacity.put("storage", storage)
        spec.capacity >> capacity
        spec.accessModes >> ["ReadWriteMany"]
        def volumeSource = Mock(V1FlockerVolumeSource)
        spec.flocker >> volumeSource
        kubePersistentVolume.spec >> spec
        def status = Mock(V1PersistentVolumeStatus)
        kubePersistentVolume.status >> status

        when:
        def pv = new PersistentVolume(kubePersistentVolume)

        then:
        pv.capacity == "1"
        pv.accessModes == "RWX"
        pv.reclaimPolicy == null
        pv.status == null
        pv.claim == null
        pv.storageClass == null
        pv.reason == null
        pv.finalizers == "[]"
        pv.deletionTimestamp == "0s"
        pv.nodeAffinity == null
        pv.message == null
        pv.source.size() == 3
    }
}
