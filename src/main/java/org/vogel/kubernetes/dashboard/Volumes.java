package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class Volumes {
    private Map<String, Map<String, String>> volumes;

    public Volumes(List<V1Volume> podVolumes) {
        if (podVolumes != null && podVolumes.size() > 0) {
            volumes = new HashMap<>();
            for (V1Volume volume : podVolumes) {
                String volumeName = volume.getName();
                Map<String, String> info = null;
                if (volume.getHostPath() != null) {
                    info = printHostPathVolumeSource(volume.getHostPath());
                } else if (volume.getEmptyDir() != null) {
                    info = printEmptyDirVolumeSource(volume.getEmptyDir());
                } else if (volume.getGcePersistentDisk() != null) {
                    info = printGCEPersistentDiskVolumeSource(volume.getGcePersistentDisk());
                } else if (volume.getAwsElasticBlockStore() != null) {
                    info = printAWSElasticBlockStoreVolumeSource(volume.getAwsElasticBlockStore());
                } else if (volume.getGitRepo() != null) {
                    info = printGitRepoVolumeSource(volume.getGitRepo());
                } else if (volume.getSecret() != null) {
                    info = printSecretVolumeSource(volume.getSecret());
                } else if (volume.getConfigMap() != null) {
                    info = printConfigMapVolumeSource(volume.getConfigMap());
                } else if (volume.getNfs() != null) {
                    info = printNFSVolumeSource(volume.getNfs());
                } else if (volume.getIscsi() != null) {
                    info = printISCSIVolumeSource(volume.getIscsi());
                } else if (volume.getGlusterfs() != null) {
                    info = printGlusterfsVolumeSource(volume.getGlusterfs());
                } else if (volume.getPersistentVolumeClaim() != null) {
                    info = printPersistentVolumeClaimVolumeSource(volume.getPersistentVolumeClaim());
                } else if (volume.getRbd() != null) {
                    info = printRBDVolumeSource(volume.getRbd());
                } else if (volume.getQuobyte() != null) {
                    info = printQuobyteVolumeSource(volume.getQuobyte());
                } else if (volume.getDownwardAPI() != null) {
                    info = printDownwardAPIVolumeSource(volume.getDownwardAPI());
                } else if (volume.getAzureDisk() != null) {
                    info = printAzureDiskVolumeSource(volume.getAzureDisk());
                } else if (volume.getVsphereVolume() != null) {
                    info = printVsphereVolumeSource(volume.getVsphereVolume());
                } else if (volume.getCinder() != null) {
                    info = printCinderVolumeSource(volume.getCinder());
                } else if (volume.getPhotonPersistentDisk() != null) {
                    info = printPhotonPersistentDiskVolumeSource(volume.getPhotonPersistentDisk());
                } else if (volume.getPortworxVolume() != null) {
                    info = printPortworxVolumeSource(volume.getPortworxVolume());
                } else if (volume.getScaleIO() != null) {
                    info = printScaleIOVolumeSource(volume.getScaleIO());
                } else if (volume.getCephfs() != null) {
                    info = printCephFSVolumeSource(volume.getCephfs());
                } else if (volume.getStorageos() != null) {
                    info = printStorageOSVolumeSource(volume.getStorageos());
                } else if (volume.getFc() != null) {
                    info = printFCVolumeSource(volume.getFc());
                } else if (volume.getAzureFile() != null) {
                    info = printAzureFileVolumeSource(volume.getAzureFile());
                } else if (volume.getFlexVolume() != null) {
                    info = printFlexVolumeSource(volume.getFlexVolume());
                } else if (volume.getFlocker() != null) {
                    info = printFlockerVolumeSource(volume.getFlocker());
                }

                volumes.put(volumeName, info);
            }
        }
    }

    private Map<String, String> printHostPathVolumeSource(V1HostPathVolumeSource hostPath) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "HostPath (bare host directory volume)");
        info.put("Path", hostPath.getPath());
        String type = hostPath.getType();
        if (isBlank(type)) {
            type = "none";
        }
        info.put("HostPathType:", type);

        return info;
    }

    private Map<String, String> printEmptyDirVolumeSource(V1EmptyDirVolumeSource emptyDir) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "EmptyDir (a temporary directory that shares a pod's lifetime)");
        info.put("Medium:", emptyDir.getMedium());

        return info;
    }

    private Map<String, String> printGCEPersistentDiskVolumeSource(V1GCEPersistentDiskVolumeSource gce) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "GCEPersistentDisk (a Persistent Disk resource in Google Compute Engine)");
        info.put("PDName:", gce.getPdName());
        info.put("FSType:", gce.getFsType());
        info.put("Partition:", gce.getPartition()
                .toString());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(gce.isReadOnly())));

        return info;
    }

    private Map<String, String> printAWSElasticBlockStoreVolumeSource(V1AWSElasticBlockStoreVolumeSource aws) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "AWSElasticBlockStore (a Persistent Disk resource in AWS)");
        info.put("VolumeID:", aws.getVolumeID());
        info.put("FSType:", aws.getFsType());
        info.put("Partition:", aws.getPartition()
                .toString());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(aws.isReadOnly())));

        return info;
    }

    private Map<String, String> printGitRepoVolumeSource(V1GitRepoVolumeSource git) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "GitRepo (a volume that is pulled from git when the pod is created)");
        info.put("Repository:", git.getRepository());
        info.put("Revision:", git.getRevision());

        return info;
    }

    private Map<String, String> printSecretVolumeSource(V1SecretVolumeSource secret) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Secret (a volume populated by a Secret)");
        info.put("SecretName", secret.getSecretName());
        info.put("Optional:", Boolean.toString(Boolean.TRUE.equals(secret.isOptional())));

        return info;
    }

    private Map<String, String> printConfigMapVolumeSource(V1ConfigMapVolumeSource configMap) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "ConfigMap (a volume populated by a ConfigMap)");
        info.put("Name:", configMap.getName());
        info.put("Optional:", Boolean.toString(Boolean.TRUE.equals(configMap.isOptional())));

        return info;
    }

    private Map<String, String> printNFSVolumeSource(V1NFSVolumeSource nfs) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "NFS (an NFS mount that lasts the lifetime of a pod)");
        info.put("Server:", nfs.getServer());
        info.put("Path:", nfs.getPath());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(nfs.isReadOnly())));

        return info;
    }

    private Map<String, String> printISCSIVolumeSource(V1ISCSIVolumeSource iscsi) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:",
                 "ISCSI (an ISCSI Disk resource that is attached to a kubelet's host machine and then exposed to the pod)");
        info.put("TargetPortal:", iscsi.getTargetPortal());
        info.put("IQN:", iscsi.getIqn());
        info.put("Lun:", iscsi.getLun()
                .toString());
        info.put("ISCSIInterface:", iscsi.getIscsiInterface());
        info.put("FSType:", iscsi.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(iscsi.isReadOnly())));
        String portals = iscsi.getPortals()
                .stream()
                .collect(joining(","));
        info.put("Portals:", portals);
        info.put("DiscoveryCHAPAuth:", Boolean.toString(Boolean.TRUE.equals(iscsi.isChapAuthDiscovery())));
        info.put("SessionCHAPAuth:", Boolean.toString(Boolean.TRUE.equals(iscsi.isChapAuthSession())));
        info.put("SecretRef:", iscsi.getSecretRef()
                .getName());
        info.put("InitiatorName:", iscsi.getInitiatorName());

        return info;
    }

    private Map<String, String> printGlusterfsVolumeSource(V1GlusterfsVolumeSource glusterfs) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Glusterfs (a Glusterfs mount on the host that shares a pod's lifetime)");
        info.put("EndpointsName:", glusterfs.getEndpoints());
        info.put("Path:", glusterfs.getPath());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(glusterfs.isReadOnly())));

        return info;
    }

    private Map<String, String> printPersistentVolumeClaimVolumeSource(V1PersistentVolumeClaimVolumeSource claim) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PersistentVolumeClaim (a reference to a PersistentVolumeClaim in the same namespace)");
        info.put("ClaimName:", claim.getClaimName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(claim.isReadOnly())));

        return info;
    }

    private Map<String, String> printRBDVolumeSource(V1RBDVolumeSource rbd) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "RBD (a Rados Block Device mount on the host that shares a pod's lifetime)");
        String monitors = rbd.getMonitors()
                .stream()
                .collect(joining(","));
        info.put("CephMonitors:", monitors);
        info.put("RBDImage:", rbd.getImage());
        info.put("FSType:", rbd.getFsType());
        info.put("RBDPool:", rbd.getPool());
        info.put("RadosUser:", rbd.getUser());
        info.put("Keyring:", rbd.getKeyring());
        info.put("SecretRef:", rbd.getSecretRef()
                .getName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(rbd.isReadOnly())));

        return info;
    }

    private Map<String, String> printQuobyteVolumeSource(V1QuobyteVolumeSource quobyte) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Quobyte (a Quobyte mount on the host that shares a pod's lifetime)");
        info.put("Registry:", quobyte.getRegistry());
        info.put("Volume:", quobyte.getVolume());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(quobyte.isReadOnly())));

        return info;
    }

    private Map<String, String> printDownwardAPIVolumeSource(V1DownwardAPIVolumeSource d) {
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

    private Map<String, String> printAzureDiskVolumeSource(V1AzureDiskVolumeSource d) {
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

    private Map<String, String> printVsphereVolumeSource(V1VsphereVirtualDiskVolumeSource vsphere) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "vSphereVolume (a Persistent Disk resource in vSphere)");
        info.put("VolumePath:", vsphere.getVolumePath());
        info.put("FSType:", vsphere.getFsType());
        info.put("StoragePolicyName:", vsphere.getStoragePolicyName());

        return info;
    }

    private Map<String, String> printCinderVolumeSource(V1CinderVolumeSource cinder) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Cinder (a Persistent Disk resource in OpenStack)");
        info.put("VolumeID:", cinder.getVolumeID());
        info.put("FSType:", cinder.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(cinder.isReadOnly())));

        return info;
    }

    private Map<String, String> printPhotonPersistentDiskVolumeSource(V1PhotonPersistentDiskVolumeSource photon) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PhotonPersistentDisk (a Persistent Disk resource in photon platform)");
        info.put("PdID:", photon.getPdID());
        info.put("FSType:", photon.getFsType());

        return info;
    }

    private Map<String, String> printPortworxVolumeSource(V1PortworxVolumeSource pwxVolume) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "PortworxVolume (a Portworx Volume resource)");
        info.put("VolumeID:", pwxVolume.getVolumeID());

        return info;
    }

    private Map<String, String> printScaleIOVolumeSource(V1ScaleIOVolumeSource sio) {
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

    private Map<String, String> printCephFSVolumeSource(V1CephFSVolumeSource cephfs) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "CephFS (a CephFS mount on the host that shares a pod's lifetime)");
        String monitors = cephfs.getMonitors()
                .stream()
                .collect(joining(","));
        info.put("Monitors:", monitors);
        info.put("Path:", cephfs.getPath());
        info.put("User:", cephfs.getUser());
        info.put("SecretFile:", cephfs.getSecretFile());
        info.put("SecretRef:", cephfs.getSecretRef()
                .getName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(cephfs.isReadOnly())));

        return info;
    }

    private Map<String, String> printStorageOSVolumeSource(V1StorageOSVolumeSource storageos) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "StorageOS (a StorageOS Persistent Disk resource)");
        info.put("VolumeName:", storageos.getVolumeName());
        info.put("VolumeNamespace:", storageos.getVolumeNamespace());
        info.put("FSType:", storageos.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(storageos.isReadOnly())));

        return info;
    }

    private Map<String, String> printFCVolumeSource(V1FCVolumeSource fc) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "FC (a Fibre Channel disk)");
        String targetWwns = fc.getTargetWWNs()
                .stream()
                .collect(joining(","));
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

    private Map<String, String> printAzureFileVolumeSource(V1AzureFileVolumeSource azureFile) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "AzureFile (an Azure File Service mount on the host and bind mount to the pod)");
        info.put("SecretName:", azureFile.getSecretName());
        info.put("ShareName:", azureFile.getShareName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(azureFile.isReadOnly())));

        return info;
    }

    private Map<String, String> printFlexVolumeSource(V1FlexVolumeSource flex) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:",
                 "FlexVolume (a generic volume resource that is provisioned/attached using an exec based plugin)");
        info.put("Driver:", flex.getDriver());
        info.put("FSType:", flex.getFsType());
        info.put("SecretRef:", flex.getSecretRef()
                .getName());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(flex.isReadOnly())));
        info.put("Options:", flex.getOptions()
                .toString());

        return info;
    }

    private Map<String, String> printFlockerVolumeSource(V1FlockerVolumeSource flocker) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Flocker (a Flocker volume mounted by the Flocker agent)");
        info.put("DatasetName:", flocker.getDatasetName());
        info.put("DatasetUUID:", flocker.getDatasetUUID());

        return info;
    }
}
