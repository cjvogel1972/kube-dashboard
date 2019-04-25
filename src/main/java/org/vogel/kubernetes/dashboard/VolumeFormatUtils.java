package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.vogel.kubernetes.dashboard.FormatUtils.joinListWithCommas;

public class VolumeFormatUtils {
    public static Map<String, String> printHostPathVolumeSource(V1HostPathVolumeSource hostPath) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "HostPath (bare host directory volume)");
        info.put("Path", hostPath.getPath());
        String type = defaultIfBlank(hostPath.getType(), "none");
        info.put("HostPathType:", type);

        return info;
    }

    public static Map<String, String> printEmptyDirVolumeSource(V1EmptyDirVolumeSource emptyDir) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "EmptyDir (a temporary directory that shares a pod's lifetime)");
        info.put("Medium:", emptyDir.getMedium());

        return info;
    }

    public static Map<String, String> printGCEPersistentDiskVolumeSource(V1GCEPersistentDiskVolumeSource gce) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "GCEPersistentDisk (a Persistent Disk resource in Google Compute Engine)");
        info.put("PDName:", gce.getPdName());
        info.put("FSType:", gce.getFsType());
        info.put("Partition:", gce.getPartition()
                .toString());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(gce.isReadOnly())));

        return info;
    }

    public static Map<String, String> printAWSElasticBlockStoreVolumeSource(V1AWSElasticBlockStoreVolumeSource aws) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "AWSElasticBlockStore (a Persistent Disk resource in AWS)");
        info.put("VolumeID:", aws.getVolumeID());
        info.put("FSType:", aws.getFsType());
        info.put("Partition:", aws.getPartition()
                .toString());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(aws.isReadOnly())));

        return info;
    }

    public static Map<String, String> printGitRepoVolumeSource(V1GitRepoVolumeSource git) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "GitRepo (a volume that is pulled from git when the pod is created)");
        info.put("Repository:", git.getRepository());
        info.put("Revision:", git.getRevision());

        return info;
    }

    public static Map<String, String> printSecretVolumeSource(V1SecretVolumeSource secret) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Secret (a volume populated by a Secret)");
        info.put("SecretName", secret.getSecretName());
        info.put("Optional:", Boolean.toString(Boolean.TRUE.equals(secret.isOptional())));

        return info;
    }

    public static Map<String, String> printConfigMapVolumeSource(V1ConfigMapVolumeSource configMap) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "ConfigMap (a volume populated by a ConfigMap)");
        info.put("Name:", configMap.getName());
        info.put("Optional:", Boolean.toString(Boolean.TRUE.equals(configMap.isOptional())));

        return info;
    }

    public static Map<String, String> printNFSVolumeSource(V1NFSVolumeSource nfs) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "NFS (an NFS mount that lasts the lifetime of a pod)");
        info.put("Server:", nfs.getServer());
        info.put("Path:", nfs.getPath());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(nfs.isReadOnly())));

        return info;
    }

    public static Map<String, String> printISCSIVolumeSource(V1ISCSIVolumeSource iscsi) {
        return printISCSI(iscsi.getTargetPortal(), iscsi.getIqn(), iscsi.getLun(), iscsi.getIscsiInterface(),
                          iscsi.getFsType(), iscsi.isReadOnly(), iscsi.getPortals(),
                          iscsi.isChapAuthDiscovery(), iscsi.isChapAuthSession(), iscsi.getSecretRef()
                                  .getName(), iscsi.getInitiatorName());
    }

    public static Map<String, String> printISCSIPersistentVolumeSource(V1ISCSIPersistentVolumeSource iscsi) {
        return printISCSI(iscsi.getTargetPortal(), iscsi.getIqn(), iscsi.getLun(), iscsi.getIscsiInterface(),
                          iscsi.getFsType(), iscsi.isReadOnly(), iscsi.getPortals(),
                          iscsi.isChapAuthDiscovery(),
                          iscsi.isChapAuthSession(), iscsi.getSecretRef()
                                  .getName(), iscsi.getInitiatorName());
    }

    private static Map<String, String> printISCSI(String targetPortal, String iqn, Integer lun,
                                                  String iscsiInterface, String fsType, Boolean readOnly,
                                                  List<String> portals2, Boolean chapAuthDiscovery,
                                                  Boolean chapAuthSession, String name, String initiatorName) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:",
                 "ISCSI (an ISCSI Disk resource that is attached to a kubelet's host machine and then exposed to the pod)");
        info.put("TargetPortal:", targetPortal);
        info.put("IQN:", iqn);
        info.put("Lun:", lun
                .toString());
        info.put("ISCSIInterface:", iscsiInterface);
        info.put("FSType:", fsType);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));
        String portals = joinListWithCommas(portals2);
        info.put("Portals:", portals);
        info.put("DiscoveryCHAPAuth:", Boolean.toString(Boolean.TRUE.equals(chapAuthDiscovery)));
        info.put("SessionCHAPAuth:", Boolean.toString(Boolean.TRUE.equals(chapAuthSession)));
        info.put("SecretRef:", name);
        info.put("InitiatorName:", initiatorName);

        return info;
    }

    public static Map<String, String> printGlusterfsVolumeSource(V1GlusterfsVolumeSource glusterfs) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Glusterfs (a Glusterfs mount on the host that shares a pod's lifetime)");
        info.put("EndpointsName:", glusterfs.getEndpoints());
        info.put("Path:", glusterfs.getPath());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(glusterfs.isReadOnly())));

        return info;
    }

    public static Map<String, String> printPersistentVolumeClaimVolumeSource(
            V1PersistentVolumeClaimVolumeSource claim) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PersistentVolumeClaim (a reference to a PersistentVolumeClaim in the same namespace)");
        info.put("ClaimName:", claim.getClaimName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(claim.isReadOnly())));

        return info;
    }

    public static Map<String, String> printRBDVolumeSource(V1RBDVolumeSource rbd) {
        return printRBD(rbd.getMonitors(), rbd.getImage(), rbd.getFsType(), rbd.getPool(), rbd.getUser(),
                        rbd.getKeyring(), rbd.getSecretRef()
                                .getName(), rbd.isReadOnly());
    }

    public static Map<String, String> printRBDPersistentVolumeSource(V1RBDPersistentVolumeSource rbd) {
        return printRBD(rbd.getMonitors(), rbd.getImage(), rbd.getFsType(), rbd.getPool(), rbd.getUser(),
                        rbd.getKeyring(),
                        rbd.getSecretRef()
                                .getName(), rbd.isReadOnly());
    }

    private static Map<String, String> printRBD(List<String> monitors2, String image, String fsType,
                                                String pool, String user, String keyring, String name,
                                                Boolean readOnly) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "RBD (a Rados Block Device mount on the host that shares a pod's lifetime)");
        String monitors = joinListWithCommas(monitors2);
        info.put("CephMonitors:", monitors);
        info.put("RBDImage:", image);
        info.put("FSType:", fsType);
        info.put("RBDPool:", pool);
        info.put("RadosUser:", user);
        info.put("Keyring:", keyring);
        info.put("SecretRef:", name);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));

        return info;
    }

    public static Map<String, String> printQuobyteVolumeSource(V1QuobyteVolumeSource quobyte) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Quobyte (a Quobyte mount on the host that shares a pod's lifetime)");
        info.put("Registry:", quobyte.getRegistry());
        info.put("Volume:", quobyte.getVolume());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(quobyte.isReadOnly())));

        return info;
    }

    public static Map<String, String> printDownwardAPIVolumeSource(V1DownwardAPIVolumeSource d) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "DownwardAPI (a volume populated by information about the pod)");
        for (V1DownwardAPIVolumeFile mapping : d.getItems()) {
            if (mapping.getFieldRef() != null) {
                info.put(mapping.getFieldRef()
                                 .getFieldPath(), mapping.getPath());
            }
            if (mapping.getResourceFieldRef() != null) {
                info.put(mapping.getResourceFieldRef()
                                 .getResource(), mapping.getPath());
            }
        }

        return info;
    }

    public static Map<String, String> printAzureDiskVolumeSource(V1AzureDiskVolumeSource d) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "AzureDisk (an Azure Data Disk mount on the host and bind mount to the pod)");
        info.put("DiskName:", d.getDiskName());
        info.put("DiskURI:", d.getDiskURI());
        info.put("Kind:", d.getKind());
        info.put("FSType:", d.getFsType());
        info.put("CachingMode:", d.getCachingMode());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(d.isReadOnly())));

        return info;
    }

    public static Map<String, String> printVsphereVolumeSource(V1VsphereVirtualDiskVolumeSource vsphere) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "vSphereVolume (a Persistent Disk resource in vSphere)");
        info.put("VolumePath:", vsphere.getVolumePath());
        info.put("FSType:", vsphere.getFsType());
        info.put("StoragePolicyName:", vsphere.getStoragePolicyName());

        return info;
    }

    public static Map<String, String> printCinderVolumeSource(V1CinderVolumeSource cinder) {
        return printCinder(cinder.getVolumeID(), cinder.getFsType(), cinder.isReadOnly());
    }

    public static Map<String, String> printCinderPersistentVolumeSource(V1CinderPersistentVolumeSource cinder) {
        return printCinder(cinder.getVolumeID(), cinder.getFsType(), cinder.isReadOnly());
    }

    private static Map<String, String> printCinder(String volumeID, String fsType, Boolean readOnly) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Cinder (a Persistent Disk resource in OpenStack)");
        info.put("VolumeID:", volumeID);
        info.put("FSType:", fsType);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));

        return info;
    }

    public static Map<String, String> printPhotonPersistentDiskVolumeSource(V1PhotonPersistentDiskVolumeSource photon) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PhotonPersistentDisk (a Persistent Disk resource in photon platform)");
        info.put("PdID:", photon.getPdID());
        info.put("FSType:", photon.getFsType());

        return info;
    }

    public static Map<String, String> printPortworxVolumeSource(V1PortworxVolumeSource pwxVolume) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PortworxVolume (a Portworx Volume resource)");
        info.put("VolumeID:", pwxVolume.getVolumeID());

        return info;
    }

    public static Map<String, String> printScaleIOVolumeSource(V1ScaleIOVolumeSource sio) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "ScaleIO (a persistent volume backed by a block device in ScaleIO)");
        info.put("Gateway:", sio.getGateway());
        info.put("System:", sio.getSystem());
        info.put("Protection Domain:", sio.getProtectionDomain());
        info.put("Storage Pool:", sio.getStoragePool());
        info.put("Storage Mode:", sio.getStorageMode());
        info.put("VolumeName:", sio.getVolumeName());
        info.put("FSType:", sio.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(sio.isReadOnly())));

        return info;
    }

    public static Map<String, String> printScaleIOPersistentVolumeSource(V1ScaleIOPersistentVolumeSource sio) {
        String secretNs = "";
        String secretName = "";

        if (sio.getSecretRef() != null) {
            secretNs = sio.getSecretRef()
                    .getNamespace();
            secretName = sio.getSecretRef()
                    .getName();
        }

        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "ScaleIO (a persistent volume backed by a block device in ScaleIO)");
        info.put("Gateway:", sio.getGateway());
        info.put("System:", sio.getSystem());
        info.put("Protection Domain:", sio.getProtectionDomain());
        info.put("Storage Pool:", sio.getStoragePool());
        info.put("Storage Mode:", sio.getStorageMode());
        info.put("VolumeName:", sio.getVolumeName());
        info.put("SecretName:", secretName);
        info.put("SecretNamespace:", secretNs);
        info.put("FSType:", sio.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(sio.isReadOnly())));

        return info;
    }

    public static Map<String, String> printCephFSVolumeSource(V1CephFSVolumeSource cephfs) {
        return printCephFS(cephfs.getMonitors(), cephfs.getPath(), cephfs.getUser(), cephfs.getSecretFile(),
                           cephfs.getSecretRef()
                                   .getName(), cephfs.isReadOnly());
    }

    public static Map<String, String> printCephFSPersistentVolumeSource(V1CephFSPersistentVolumeSource cephfs) {
        return printCephFS(cephfs.getMonitors(), cephfs.getPath(), cephfs.getUser(), cephfs.getSecretFile(),
                           cephfs.getSecretRef()
                                   .getName(), cephfs.isReadOnly());
    }

    private static Map<String, String> printCephFS(List<String> cephMonitors, String path, String user,
                                                   String secretFile, String name, Boolean readOnly) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "CephFS (a CephFS mount on the host that shares a pod's lifetime)");
        String monitors = joinListWithCommas(cephMonitors);
        info.put("Monitors:", monitors);
        info.put("Path:", path);
        info.put("User:", user);
        info.put("SecretFile:", secretFile);
        info.put("SecretRef:", name);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));

        return info;
    }

    public static Map<String, String> printStorageOSVolumeSource(V1StorageOSVolumeSource storageos) {
        return printStorageOS(storageos.getVolumeName(), storageos.getVolumeNamespace(), storageos.getFsType(),
                              storageos.isReadOnly());
    }

    public static Map<String, String> printStorageOSPersistentVolumeSource(
            V1StorageOSPersistentVolumeSource storageos) {
        return printStorageOS(storageos.getVolumeName(), storageos.getVolumeNamespace(), storageos.getFsType(),
                              storageos.isReadOnly());
    }

    private static Map<String, String> printStorageOS(String volumeName, String volumeNamespace, String fsType,
                                                      Boolean readOnly) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "StorageOS (a StorageOS Persistent Disk resource)");
        info.put("VolumeName:", volumeName);
        info.put("VolumeNamespace:", volumeNamespace);
        info.put("FSType:", fsType);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));

        return info;
    }

    public static Map<String, String> printFCVolumeSource(V1FCVolumeSource fc) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "FC (a Fibre Channel disk)");
        String targetWwns = joinListWithCommas(fc.getTargetWWNs());
        info.put("TargetWWNs:", targetWwns);
        String lun = null;
        if (fc.getLun() != null) {
            lun = fc.getLun()
                    .toString();
        }
        info.put("LUN:", lun);
        info.put("FSType:", fc.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(fc.isReadOnly())));

        return info;
    }

    public static Map<String, String> printAzureFileVolumeSource(V1AzureFileVolumeSource azureFile) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "AzureFile (an Azure File Service mount on the host and bind mount to the pod)");
        info.put("SecretName:", azureFile.getSecretName());
        info.put("ShareName:", azureFile.getShareName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(azureFile.isReadOnly())));

        return info;
    }

    public static Map<String, String> printAzureFilePersistentVolumeSource(
            V1AzureFilePersistentVolumeSource azureFile) {
        Map<String, String> info = new LinkedHashMap<>();
        String ns = "";
        if (azureFile.getSecretNamespace() != null) {
            ns = azureFile.getSecretNamespace();
        }
        info.put("Type:", "AzureFile (an Azure File Service mount on the host and bind mount to the pod)");
        info.put("SecretNamespace:", ns);
        info.put("SecretName:", azureFile.getSecretName());
        info.put("ShareName:", azureFile.getShareName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(azureFile.isReadOnly())));

        return info;
    }

    public static Map<String, String> printFlexVolumeSource(V1FlexVolumeSource flex) {
        return printStorageOS(flex.getDriver(), flex.getFsType(), flex.getSecretRef()
                .getName(), flex.isReadOnly(), flex.getOptions());
    }

    public static Map<String, String> printFlexPersistentVolumeSource(V1FlexPersistentVolumeSource flex) {
        return printStorageOS(flex.getDriver(), flex.getFsType(), flex.getSecretRef()
                .getName(), flex.isReadOnly(), flex.getOptions());
    }

    private static Map<String, String> printStorageOS(String driver, String fsType, String name, Boolean readOnly,
                                                      Map<String, String> options) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:",
                 "FlexVolume (a generic volume resource that is provisioned/attached using an exec based plugin)");
        info.put("Driver:", driver);
        info.put("FSType:", fsType);
        info.put("SecretRef:", name);
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(readOnly)));
        info.put("Options:", options
                .toString());

        return info;
    }

    public static Map<String, String> printFlockerVolumeSource(V1FlockerVolumeSource flocker) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Flocker (a Flocker volume mounted by the Flocker agent)");
        info.put("DatasetName:", flocker.getDatasetName());
        info.put("DatasetUUID:", flocker.getDatasetUUID());

        return info;
    }
}
