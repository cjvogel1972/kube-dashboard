package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.*
import spock.lang.Specification

class VolumeFormatUtilsSpec extends Specification {
    def "test host path"() {
        given:
        def volumeSource = Mock(V1HostPathVolumeSource)

        when:
        def result = VolumeFormatUtils.printHostPathVolumeSource(volumeSource)

        then:
        result["Type:"] == "HostPath (bare host directory volume)"
        result["Path"] == null
        result["HostPathType:"] == "none"
    }

    def "test GCE disk"() {
        given:
        def volumeSource = Mock(V1GCEPersistentDiskVolumeSource)
        volumeSource.partition >> 1

        when:
        def result = VolumeFormatUtils.printGCEPersistentDiskVolumeSource(volumeSource)

        then:
        result["Type:"] == "GCEPersistentDisk (a Persistent Disk resource in Google Compute Engine)"
        result["PDName:"] == null
        result["FSType:"] == null
        result["Partition:"] == "1"
        result["ReadOnly:"] == "false"
    }

    def "test AWS elastic block"() {
        given:
        def volumeSource = Mock(V1AWSElasticBlockStoreVolumeSource)
        volumeSource.partition >> 1

        when:
        def result = VolumeFormatUtils.printAWSElasticBlockStoreVolumeSource(volumeSource)

        then:
        result["Type:"] == "AWSElasticBlockStore (a Persistent Disk resource in AWS)"
        result["VolumeID:"] == null
        result["FSType:"] == null
        result["Partition:"] == "1"
        result["ReadOnly:"] == "false"
    }

    def "test NFS"() {
        given:
        def volumeSource = Mock(V1NFSVolumeSource)

        when:
        def result = VolumeFormatUtils.printNFSVolumeSource(volumeSource)

        then:
        result["Type:"] == "NFS (an NFS mount that lasts the lifetime of a pod)"
        result["Server:"] == null
        result["Path:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test iSCSI persistent volume"() {
        given:
        def volumeSource = Mock(V1ISCSIPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.lun >> 1
        volumeSource.portals >> []

        when:
        def result = VolumeFormatUtils.printISCSIPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "ISCSI (an ISCSI Disk resource that is attached to a kubelet's host machine and then exposed to the pod)"
        result["TargetPortal:"] == null
        result["IQN:"] == null
        result["Lun:"] == "1"
        result["ISCSIInterface:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
        result["Portals:"] == ""
        result["DiscoveryCHAPAuth:"] == "false"
        result["SessionCHAPAuth:"] == "false"
        result["SecretRef:"] == null
        result["InitiatorName:"] == null
    }

    def "test iSCSI volume"() {
        given:
        def volumeSource = Mock(V1ISCSIVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.lun >> 1
        volumeSource.portals >> []

        when:
        def result = VolumeFormatUtils.printISCSIVolumeSource(volumeSource)

        then:
        result["Type:"] == "ISCSI (an ISCSI Disk resource that is attached to a kubelet's host machine and then exposed to the pod)"
        result["TargetPortal:"] == null
        result["IQN:"] == null
        result["Lun:"] == "1"
        result["ISCSIInterface:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
        result["Portals:"] == ""
        result["DiscoveryCHAPAuth:"] == "false"
        result["SessionCHAPAuth:"] == "false"
        result["SecretRef:"] == null
        result["InitiatorName:"] == null
    }

    def "test Glusterfs"() {
        given:
        def volumeSource = Mock(V1GlusterfsVolumeSource)

        when:
        def result = VolumeFormatUtils.printGlusterfsVolumeSource(volumeSource)

        then:
        result["Type:"] == "Glusterfs (a Glusterfs mount on the host that shares a pod's lifetime)"
        result["EndpointsName:"] == null
        result["Path:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test RBD persistent volume"() {
        given:
        def volumeSource = Mock(V1RBDPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.monitors >> []

        when:
        def result = VolumeFormatUtils.printRBDPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "RBD (a Rados Block Device mount on the host that shares a pod's lifetime)"
        result["CephMonitors:"] == ""
        result["RBDImage:"] == null
        result["FSType:"] == null
        result["RBDPool:"] == null
        result["RadosUser:"] == null
        result["Keyring:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test RBD volume"() {
        given:
        def volumeSource = Mock(V1RBDVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.monitors >> []

        when:
        def result = VolumeFormatUtils.printRBDVolumeSource(volumeSource)

        then:
        result["Type:"] == "RBD (a Rados Block Device mount on the host that shares a pod's lifetime)"
        result["CephMonitors:"] == ""
        result["RBDImage:"] == null
        result["FSType:"] == null
        result["RBDPool:"] == null
        result["RadosUser:"] == null
        result["Keyring:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Quobyte"() {
        given:
        def volumeSource = Mock(V1QuobyteVolumeSource)

        when:
        def result = VolumeFormatUtils.printQuobyteVolumeSource(volumeSource)

        then:
        result["Type:"] == "Quobyte (a Quobyte mount on the host that shares a pod's lifetime)"
        result["Registry:"] == null
        result["Volume:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Azure disk"() {
        given:
        def volumeSource = Mock(V1AzureDiskVolumeSource)

        when:
        def result = VolumeFormatUtils.printAzureDiskVolumeSource(volumeSource)

        then:
        result["Type:"] == "AzureDisk (an Azure Data Disk mount on the host and bind mount to the pod)"
        result["DiskName:"] == null
        result["DiskURI:"] == null
        result["Kind:"] == null
        result["FSType:"] == null
        result["CachingMode:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test vSphere"() {
        given:
        def volumeSource = Mock(V1VsphereVirtualDiskVolumeSource)

        when:
        def result = VolumeFormatUtils.printVsphereVolumeSource(volumeSource)

        then:
        result["Type:"] == "vSphereVolume (a Persistent Disk resource in vSphere)"
        result["VolumePath:"] == null
        result["FSType:"] == null
        result["StoragePolicyName:"] == null
    }

    def "test Cinder persistent volume"() {
        given:
        def volumeSource = Mock(V1CinderPersistentVolumeSource)

        when:
        def result = VolumeFormatUtils.printCinderPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "Cinder (a Persistent Disk resource in OpenStack)"
        result["VolumeID:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Cinder volume"() {
        given:
        def volumeSource = Mock(V1CinderVolumeSource)

        when:
        def result = VolumeFormatUtils.printCinderVolumeSource(volumeSource)

        then:
        result["Type:"] == "Cinder (a Persistent Disk resource in OpenStack)"
        result["VolumeID:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Photon"() {
        given:
        def volumeSource = Mock(V1PhotonPersistentDiskVolumeSource)

        when:
        def result = VolumeFormatUtils.printPhotonPersistentDiskVolumeSource(volumeSource)

        then:
        result["Type:"] == "PhotonPersistentDisk (a Persistent Disk resource in photon platform)"
        result["PdID:"] == null
        result["FSType:"] == null
    }

    def "test Portworx"() {
        given:
        def volumeSource = Mock(V1PortworxVolumeSource)

        when:
        def result = VolumeFormatUtils.printPortworxVolumeSource(volumeSource)

        then:
        result["Type:"] == "PortworxVolume (a Portworx Volume resource)"
        result["VolumeID:"] == null
    }

    def "test ScaleIO persistent volume"() {
        given:
        def volumeSource = Mock(V1ScaleIOPersistentVolumeSource)

        when:
        def result = VolumeFormatUtils.printScaleIOPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "ScaleIO (a persistent volume backed by a block device in ScaleIO)"
        result["Gateway:"] == null
        result["System:"] == null
        result["Protection Domain:"] == null
        result["Storage Pool:"] == null
        result["Storage Mode:"] == null
        result["VolumeName:"] == null
        result["SecretName:"] == ""
        result["SecretNamespace:"] == ""
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test ScaleIO persistent volume with secret ref"() {
        given:
        def volumeSource = Mock(V1ScaleIOPersistentVolumeSource)
        def secretRef = Mock(V1SecretReference)
        secretRef.namespace >> "foo"
        secretRef.name >> "bar"
        volumeSource.secretRef >> secretRef

        when:
        def result = VolumeFormatUtils.printScaleIOPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "ScaleIO (a persistent volume backed by a block device in ScaleIO)"
        result["Gateway:"] == null
        result["System:"] == null
        result["Protection Domain:"] == null
        result["Storage Pool:"] == null
        result["Storage Mode:"] == null
        result["VolumeName:"] == null
        result["SecretName:"] == "bar"
        result["SecretNamespace:"] == "foo"
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test ScaleIO volume"() {
        given:
        def volumeSource = Mock(V1ScaleIOVolumeSource)

        when:
        def result = VolumeFormatUtils.printScaleIOVolumeSource(volumeSource)

        then:
        result["Type:"] == "ScaleIO (a persistent volume backed by a block device in ScaleIO)"
        result["Gateway:"] == null
        result["System:"] == null
        result["Protection Domain:"] == null
        result["Storage Pool:"] == null
        result["Storage Mode:"] == null
        result["VolumeName:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Cephfs persistent volume"() {
        given:
        def volumeSource = Mock(V1CephFSPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.monitors >> []

        when:
        def result = VolumeFormatUtils.printCephFSPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "CephFS (a CephFS mount on the host that shares a pod's lifetime)"
        result["Monitors:"] == ""
        result["Path:"] == null
        result["User:"] == null
        result["SecretFile:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Cephfs volume"() {
        given:
        def volumeSource = Mock(V1CephFSVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.monitors >> []

        when:
        def result = VolumeFormatUtils.printCephFSVolumeSource(volumeSource)

        then:
        result["Type:"] == "CephFS (a CephFS mount on the host that shares a pod's lifetime)"
        result["Monitors:"] == ""
        result["Path:"] == null
        result["User:"] == null
        result["SecretFile:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test StorageOS persistent volume"() {
        given:
        def volumeSource = Mock(V1StorageOSPersistentVolumeSource)

        when:
        def result = VolumeFormatUtils.printStorageOSPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "StorageOS (a StorageOS Persistent Disk resource)"
        result["VolumeName:"] == null
        result["VolumeNamespace:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test StorageOS volume"() {
        given:
        def volumeSource = Mock(V1StorageOSVolumeSource)

        when:
        def result = VolumeFormatUtils.printStorageOSVolumeSource(volumeSource)

        then:
        result["Type:"] == "StorageOS (a StorageOS Persistent Disk resource)"
        result["VolumeName:"] == null
        result["VolumeNamespace:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test FC"() {
        given:
        def volumeSource = Mock(V1FCVolumeSource)
        volumeSource.targetWWNs >> []

        when:
        def result = VolumeFormatUtils.printFCVolumeSource(volumeSource)

        then:
        result["Type:"] == "FC (a Fibre Channel disk)"
        result["TargetWWNs:"] == ""
        result["LUN:"] == null
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test FC with LUN"() {
        given:
        def volumeSource = Mock(V1FCVolumeSource)
        volumeSource.targetWWNs >> []
        volumeSource.lun >> 1

        when:
        def result = VolumeFormatUtils.printFCVolumeSource(volumeSource)

        then:
        result["Type:"] == "FC (a Fibre Channel disk)"
        result["TargetWWNs:"] == ""
        result["LUN:"] == "1"
        result["FSType:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Azure file persistent volume"() {
        given:
        def volumeSource = Mock(V1AzureFilePersistentVolumeSource)

        when:
        def result = VolumeFormatUtils.printAzureFilePersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "AzureFile (an Azure File Service mount on the host and bind mount to the pod)"
        result["SecretNamespace:"] == ""
        result["SecretName:"] == null
        result["ShareName:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Azure file persistent volume with secret namespace"() {
        given:
        def volumeSource = Mock(V1AzureFilePersistentVolumeSource)
        volumeSource.secretNamespace >> "foo"

        when:
        def result = VolumeFormatUtils.printAzureFilePersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "AzureFile (an Azure File Service mount on the host and bind mount to the pod)"
        result["SecretNamespace:"] == "foo"
        result["SecretName:"] == null
        result["ShareName:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Azure file volume"() {
        given:
        def volumeSource = Mock(V1AzureFileVolumeSource)

        when:
        def result = VolumeFormatUtils.printAzureFileVolumeSource(volumeSource)

        then:
        result["Type:"] == "AzureFile (an Azure File Service mount on the host and bind mount to the pod)"
        result["SecretName:"] == null
        result["ShareName:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test Flex persistent volume"() {
        given:
        def volumeSource = Mock(V1FlexPersistentVolumeSource)
        volumeSource.secretRef >> Mock(V1SecretReference)
        volumeSource.options >> [:]

        when:
        def result = VolumeFormatUtils.printFlexPersistentVolumeSource(volumeSource)

        then:
        result["Type:"] == "FlexVolume (a generic volume resource that is provisioned/attached using an exec based plugin)"
        result["Driver:"] == null
        result["FSType:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
        result["Options:"] == "{}"
    }

    def "test Flex volume"() {
        given:
        def volumeSource = Mock(V1FlexVolumeSource)
        volumeSource.secretRef >> Mock(V1LocalObjectReference)
        volumeSource.options >> [:]

        when:
        def result = VolumeFormatUtils.printFlexVolumeSource(volumeSource)

        then:
        result["Type:"] == "FlexVolume (a generic volume resource that is provisioned/attached using an exec based plugin)"
        result["Driver:"] == null
        result["FSType:"] == null
        result["SecretRef:"] == null
        result["ReadOnly:"] == "false"
        result["Options:"] == "{}"
    }

    def "test Flocker"() {
        given:
        def volumeSource = Mock(V1FlockerVolumeSource)

        when:
        def result = VolumeFormatUtils.printFlockerVolumeSource(volumeSource)

        then:
        result["Type:"] == "Flocker (a Flocker volume mounted by the Flocker agent)"
        result["DatasetName:"] == null
        result["DatasetUUID:"] == null
    }

    def "test empty dir"() {
        given:
        def volumeSource = Mock(V1EmptyDirVolumeSource)

        when:
        def result = VolumeFormatUtils.printEmptyDirVolumeSource(volumeSource)

        then:
        result["Type:"] == "EmptyDir (a temporary directory that shares a pod's lifetime)"
        result["Medium:"] == null
    }

    def "test git repo"() {
        given:
        def volumeSource = Mock(V1GitRepoVolumeSource)

        when:
        def result = VolumeFormatUtils.printGitRepoVolumeSource(volumeSource)

        then:
        result["Type:"] == "GitRepo (a volume that is pulled from git when the pod is created)"
        result["Repository:"] == null
        result["Revision:"] == null
    }

    def "test secret volume"() {
        given:
        def volumeSource = Mock(V1SecretVolumeSource)

        when:
        def result = VolumeFormatUtils.printSecretVolumeSource(volumeSource)

        then:
        result["Type:"] == "Secret (a volume populated by a Secret)"
        result["SecretName:"] == null
        result["Optional:"] == "false"
    }

    def "test config map volume"() {
        given:
        def volumeSource = Mock(V1ConfigMapVolumeSource)

        when:
        def result = VolumeFormatUtils.printConfigMapVolumeSource(volumeSource)

        then:
        result["Type:"] == "ConfigMap (a volume populated by a ConfigMap)"
        result["SecretName:"] == null
        result["Optional:"] == "false"
    }

    def "test persistent volume claim volume"() {
        given:
        def volumeSource = Mock(V1PersistentVolumeClaimVolumeSource)

        when:
        def result = VolumeFormatUtils.printPersistentVolumeClaimVolumeSource(volumeSource)

        then:
        result["Type:"] == "PersistentVolumeClaim (a reference to a PersistentVolumeClaim in the same namespace)"
        result["ClaimName:"] == null
        result["ReadOnly:"] == "false"
    }

    def "test downward API volume"() {
        given:
        def volumeSource = Mock(V1DownwardAPIVolumeSource)
        volumeSource.items >> []

        when:
        def result = VolumeFormatUtils.printDownwardAPIVolumeSource(volumeSource)

        then:
        result["Type:"] == "DownwardAPI (a volume populated by information about the pod)"
    }

    def "test downward API volume with items"() {
        given:
        def volumeSource = Mock(V1DownwardAPIVolumeSource)
        def item = Mock(V1DownwardAPIVolumeFile)
        item.path >> "foo"
        def fieldRef = Mock(V1ObjectFieldSelector)
        fieldRef.fieldPath >> "fieldPath"
        item.fieldRef >> fieldRef
        def resourceFieldRef = Mock(V1ResourceFieldSelector)
        resourceFieldRef.resource >> "resource"
        item.resourceFieldRef >> resourceFieldRef
        volumeSource.items >> [item]

        when:
        def result = VolumeFormatUtils.printDownwardAPIVolumeSource(volumeSource)

        then:
        result["Type:"] == "DownwardAPI (a volume populated by information about the pod)"
        result["fieldPath"] == "foo"
        result["resource"] == "foo"
    }

    def "test downward API volume with empty item"() {
        given:
        def volumeSource = Mock(V1DownwardAPIVolumeSource)
        def item = Mock(V1DownwardAPIVolumeFile)
        volumeSource.items >> [item]

        when:
        def result = VolumeFormatUtils.printDownwardAPIVolumeSource(volumeSource)

        then:
        result["Type:"] == "DownwardAPI (a volume populated by information about the pod)"
    }
}
