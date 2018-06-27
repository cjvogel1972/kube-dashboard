package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.Nullable;
import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FormatUtils {
    public static String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return shortHumanDuration(duration);
    }

    public static List<String> printMultiline(Map<String, String> data) {
        List<String> result = null;

        if (data != null && data.size() > 0) {
            result = data.keySet()
                    .stream()
                    .sorted()
                    .map(key -> String.format("%s=%s", key, data.get(key)))
                    .collect(toList());
        }

        return result;
    }

    public static String formatLabelSelector(@Nullable V1LabelSelector labelSelector) {
        String result;

        int matchLabelsSize = 0;
        int matchExpressionsSize = 0;
        if (labelSelector != null) {
            if (labelSelector.getMatchLabels() != null) {
                matchLabelsSize = labelSelector.getMatchLabels()
                        .size();
            }
            if (labelSelector.getMatchExpressions() != null) {
                matchExpressionsSize = labelSelector.getMatchExpressions()
                        .size();
            }
        }

        try {
            if (labelSelector == null) {
                result = "";
            } else if (matchLabelsSize + matchExpressionsSize == 0) {
                result = "";
            } else {
                Selector selector = new Selector();
                if (labelSelector.getMatchLabels() != null) {
                    for (Map.Entry<String, String> entry : labelSelector.getMatchLabels()
                            .entrySet()) {
                        List<String> values = Collections.singletonList(entry.getValue());
                        Requirement requirement = new Requirement(entry.getKey(), "=", values);
                        selector.add(requirement);
                    }
                }
                if (labelSelector.getMatchExpressions() != null) {
                    for (V1LabelSelectorRequirement expression : labelSelector.getMatchExpressions()) {
                        String op;
                        switch (expression.getOperator()) {
                            case "In":
                                op = "in";
                                break;
                            case "NotIn":
                                op = "notin";
                                break;
                            case "Exists":
                                op = "exists";
                                break;
                            case "DoesNotExist":
                                op = "!";
                                break;
                            default:
                                String msg = String.format("%s is not a valid pod selector operator",
                                                           expression.getOperator());
                                throw new RequirementException(msg);
                        }
                        Requirement requirement = new Requirement(expression.getKey(), op, expression.getValues());
                        selector.add(requirement);
                    }
                }
                result = selector.string();
                if (result.length() == 0) {
                    result = "<none>";
                }
            }
        } catch (RequirementException e) {
            result = "<error>";
        }

        return result;
    }

    private static String shortHumanDuration(Duration d) {
        String result;

        if (d.getStandardDays() > 365) {
            result = format("%dy", d.getStandardDays() / 365);
        } else if (d.getStandardDays() > 0) {
            result = format("%dd", d.getStandardDays());
        } else if (d.getStandardHours() > 0) {
            result = format("%dh", d.getStandardHours());
        } else if (d.getStandardMinutes() > 0) {
            result = format("%dm", d.getStandardMinutes());
        } else {
            result = format("%ds", d.getStandardSeconds());
        }

        return result;
    }

    public static String describeBackend(String namespace, String serviceName, String servicePort,
                                         KubernetesUtils kubernetesUtils) throws ApiException {
        V1EndpointsList endpointsList = kubernetesUtils.getEndpoint(namespace, serviceName);
        V1Endpoints v1Endpoints = null;
        if (endpointsList.getItems()
                .size() > 0) {
            v1Endpoints = endpointsList.getItems()
                    .get(0);
        }
        String spName = "";
        try {
            V1Service service = kubernetesUtils.getKubeService(namespace, serviceName);
            List<V1ServicePort> ports = service.getSpec()
                    .getPorts();
            for (V1ServicePort port : ports) {
                if (StringUtils.equalsAny(servicePort, port.getName(), port.getPort()
                        .toString())) {
                    spName = port.getName();
                }
            }
        } catch (ApiException e) {
//            e.printStackTrace();
        }

        return formatEndpoints(v1Endpoints, spName);
    }

    public static String formatEndpoints(V1Endpoints v1Endpoints, String name) {
        if (v1Endpoints == null) {
            return "<none>";
        }

        List<V1EndpointSubset> subsets = v1Endpoints.getSubsets();
        if (subsets.size() == 0) {
            return "<none>";
        }

        List<String> list = new ArrayList<>();
        for (V1EndpointSubset ss : subsets) {
            List<V1EndpointPort> ports = ss.getPorts();
            for (V1EndpointPort endpointPort : ports) {
                if (StringUtils.isEmpty(name) || name.equals(endpointPort.getName())) {
                    List<V1EndpointAddress> addresses = ss.getAddresses();
                    for (V1EndpointAddress address : addresses) {
                        String hostPort = String.format("%s:%s", address.getIp(), endpointPort.getPort()
                                .toString());
                        list.add(hostPort);
                    }
                }
            }
        }

        return list.stream()
                .collect(joining(","));
    }

    public static Map<String, String> printHostPathVolumeSource(V1HostPathVolumeSource hostPath) {
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
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Cinder (a Persistent Disk resource in OpenStack)");
        info.put("VolumeID:", cinder.getVolumeID());
        info.put("FSType:", cinder.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(cinder.isReadOnly())));

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

    public static Map<String, String> printCephFSPersistentVolumeSource(V1CephFSPersistentVolumeSource cephfs) {
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

    public static Map<String, String> printStorageOSVolumeSource(V1StorageOSVolumeSource storageos) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "StorageOS (a StorageOS Persistent Disk resource)");
        info.put("VolumeName:", storageos.getVolumeName());
        info.put("VolumeNamespace:", storageos.getVolumeNamespace());
        info.put("FSType:", storageos.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(storageos.isReadOnly())));

        return info;
    }

    public static Map<String, String> printStorageOSPersistentVolumeSource(
            V1StorageOSPersistentVolumeSource storageos) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "StorageOS (a StorageOS Persistent Disk resource)");
        info.put("VolumeName:", storageos.getVolumeName());
        info.put("VolumeNamespace:", storageos.getVolumeNamespace());
        info.put("FSType:", storageos.getFsType());
        info.put("ReadOnly:", Boolean.toString(Boolean.TRUE.equals(storageos.isReadOnly())));

        return info;
    }

    public static Map<String, String> printFCVolumeSource(V1FCVolumeSource fc) {
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

    public static Map<String, String> printFlockerVolumeSource(V1FlockerVolumeSource flocker) {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Type:", "Flocker (a Flocker volume mounted by the Flocker agent)");
        info.put("DatasetName:", flocker.getDatasetName());
        info.put("DatasetUUID:", flocker.getDatasetUUID());

        return info;
    }

    public static String getAccessModesAsString(List<String> accessModes) {
        Set<String> modes = new HashSet<>(accessModes);
        List<String> modesStr = new ArrayList<>();
        if (modes.contains("ReadWriteOnce")) {
            modesStr.add("RWO");
        }
        if (modes.contains("ReadOnlyMany")) {
            modesStr.add("ROX");
        }
        if (modes.contains("ReadWriteMany")) {
            modesStr.add("RWX");
        }

        return modesStr.stream()
                .collect(joining(","));
    }
}
