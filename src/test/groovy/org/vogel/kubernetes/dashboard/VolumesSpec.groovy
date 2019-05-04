package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.*
import spock.lang.Specification

class VolumesSpec extends Specification {
    def "create Volumes host path"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1HostPathVolumeSource)
        volume.hostPath >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes empty dir"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1EmptyDirVolumeSource)
        volume.emptyDir >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 2
    }

    def "create Volumes GCE disk"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1GCEPersistentDiskVolumeSource)
        volumeSource.partition >> 1
        volume.gcePersistentDisk >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 5
    }

    def "create Volumes AWS elastic block"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1AWSElasticBlockStoreVolumeSource)
        volumeSource.partition >> 1
        volume.awsElasticBlockStore >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 5
    }

    def "create Volumes git repo"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1GitRepoVolumeSource)
        volume.gitRepo >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes secret"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1SecretVolumeSource)
        volume.secret >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes config map"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1ConfigMapVolumeSource)
        volume.configMap >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes NFS"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1NFSVolumeSource)
        volume.nfs >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes iSCSI"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1ISCSIVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.lun >> 1
        volumeSource.portals >> []
        volume.iscsi >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 12
    }

    def "create Volumes Glusterfs"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1GlusterfsVolumeSource)
        volume.glusterfs >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes persistent volume claim"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1PersistentVolumeClaimVolumeSource)
        volume.persistentVolumeClaim >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes RBD"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1RBDVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.monitors >> []
        volume.rbd >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 9
    }

    def "create Volumes Quobyte"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1QuobyteVolumeSource)
        volume.quobyte >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes downward API"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1DownwardAPIVolumeSource)
        volumeSource.items >> []
        volume.downwardAPI >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 1
    }

    def "create Volumes Azure disk"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1AzureDiskVolumeSource)
        volume.azureDisk >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 7
    }

    def "create Volumes vSphere"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1VsphereVirtualDiskVolumeSource)
        volume.vsphereVolume >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes Cinder"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1CinderVolumeSource)
        volume.cinder >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes Photon"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1PhotonPersistentDiskVolumeSource)
        volume.photonPersistentDisk >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }

    def "create Volumes Portworx"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1PortworxVolumeSource)
        volume.portworxVolume >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 2
    }

    def "create Volumes ScaleIO"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1ScaleIOVolumeSource)
        volume.scaleIO >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 9
    }

    def "create Volumes Cephfs"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1CephFSVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.monitors >> []
        volume.cephfs >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 7
    }

    def "create Volumes StorageOS"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1StorageOSVolumeSource)
        volume.storageos >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 5
    }

    def "create Volumes FC"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1FCVolumeSource)
        volumeSource.targetWWNs >> []
        volume.fc >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 5
    }

    def "create Volumes Azure file"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1AzureFileVolumeSource)
        volume.azureFile >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 4
    }

    def "create Volumes Flex"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1FlexVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.options >> [:]
        volume.flexVolume >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 6
    }

    def "create Volumes Flocker"() {
        given:
        def podVolumes = []
        def volume = Mock(V1Volume)
        volume.name >> "foo"
        def volumeSource = Mock(V1FlockerVolumeSource)
        volume.flocker >> volumeSource
        podVolumes << volume

        when:
        def volumes = new Volumes(podVolumes)

        then:
        volumes.volumes["foo"].size() == 3
    }
}
