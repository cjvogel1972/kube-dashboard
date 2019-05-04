package org.vogel.kubernetes.dashboard.persistentvolume;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.*;
import lombok.Getter;
import org.vogel.kubernetes.dashboard.Metadata;

import java.util.Map;

import static org.vogel.kubernetes.dashboard.FormatUtils.getAccessModesAsString;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;
import static org.vogel.kubernetes.dashboard.VolumeFormatUtils.*;

@Getter
public class PersistentVolume extends Metadata {
    private String capacity;
    private String accessModes;
    private String reclaimPolicy;
    private String status;
    private String claim;
    private String storageClass;
    private String reason;
    private String finalizers;
    private String deletionTimestamp;
    private String nodeAffinity;
    private String message;
    private Map<String, String> source;

    public PersistentVolume(V1PersistentVolume pv) {
        super(pv.getMetadata());
        V1ObjectMeta metadata = pv.getMetadata();
        V1PersistentVolumeSpec pvSpec = pv.getSpec();
        V1PersistentVolumeStatus pvStatus = pv.getStatus();

        Quantity quantity = pvSpec.getCapacity()
                .get("storage");
        capacity = quantity.toSuffixedString();
        accessModes = getAccessModesAsString(pvSpec.getAccessModes());
        reclaimPolicy = pvSpec.getPersistentVolumeReclaimPolicy();
        if (metadata.getDeletionTimestamp() != null) {
            status = "Terminating";
        } else {
            status = pvStatus.getPhase();
        }
        V1ObjectReference claimRef = pvSpec.getClaimRef();
        if (claimRef != null) {
            claim = String.format("%s/%s", claimRef.getNamespace(), claimRef.getName());
        }
        storageClass = getPersistentVolumeClass(pv);
        reason = pvStatus.getReason();

        if (metadata.getFinalizers() == null) {
            finalizers = "[]";
        } else {
            finalizers = metadata.getFinalizers()
                    .toString();
        }
        deletionTimestamp = translateTimestamp(metadata.getDeletionTimestamp());
        message = pvStatus.getMessage();
        source = determineSource(pvSpec);
    }

    private String getPersistentVolumeClass(V1PersistentVolume pv) {
        Map<String, String> annotations = pv.getMetadata()
                .getAnnotations();
        if (annotations.containsKey("volume.beta.kubernetes.io/storage-class")) {
            return annotations.get("volume.beta.kubernetes.io/storage-class");
        }

        return pv.getSpec()
                .getStorageClassName();
    }

    private Map<String, String> determineSource(V1PersistentVolumeSpec pvSpec) {
        Map<String, String> info = null;
        if (pvSpec.getHostPath() != null) {
            info = printHostPathVolumeSource(pvSpec.getHostPath());
        } else if (pvSpec.getGcePersistentDisk() != null) {
            info = printGCEPersistentDiskVolumeSource(pvSpec.getGcePersistentDisk());
        } else if (pvSpec.getAwsElasticBlockStore() != null) {
            info = printAWSElasticBlockStoreVolumeSource(pvSpec.getAwsElasticBlockStore());
        } else if (pvSpec.getNfs() != null) {
            info = printNFSVolumeSource(pvSpec.getNfs());
        } else if (pvSpec.getIscsi() != null) {
            info = printISCSIPersistentVolumeSource(pvSpec.getIscsi());
        } else if (pvSpec.getGlusterfs() != null) {
            info = printGlusterfsVolumeSource(pvSpec.getGlusterfs());
        } else if (pvSpec.getRbd() != null) {
            info = printRBDPersistentVolumeSource(pvSpec.getRbd());
        } else if (pvSpec.getQuobyte() != null) {
            info = printQuobyteVolumeSource(pvSpec.getQuobyte());
        } else if (pvSpec.getAzureDisk() != null) {
            info = printAzureDiskVolumeSource(pvSpec.getAzureDisk());
        } else if (pvSpec.getVsphereVolume() != null) {
            info = printVsphereVolumeSource(pvSpec.getVsphereVolume());
        } else if (pvSpec.getCinder() != null) {
            info = printCinderPersistentVolumeSource(pvSpec.getCinder());
        } else if (pvSpec.getPhotonPersistentDisk() != null) {
            info = printPhotonPersistentDiskVolumeSource(pvSpec.getPhotonPersistentDisk());
        } else if (pvSpec.getPortworxVolume() != null) {
            info = printPortworxVolumeSource(pvSpec.getPortworxVolume());
        } else if (pvSpec.getScaleIO() != null) {
            info = printScaleIOPersistentVolumeSource(pvSpec.getScaleIO());
        } else if (pvSpec.getCephfs() != null) {
            info = printCephFSPersistentVolumeSource(pvSpec.getCephfs());
        } else if (pvSpec.getStorageos() != null) {
            info = printStorageOSPersistentVolumeSource(pvSpec.getStorageos());
        } else if (pvSpec.getFc() != null) {
            info = printFCVolumeSource(pvSpec.getFc());
        } else if (pvSpec.getAzureFile() != null) {
            info = printAzureFilePersistentVolumeSource(pvSpec.getAzureFile());
        } else if (pvSpec.getFlexVolume() != null) {
            info = printFlexPersistentVolumeSource(pvSpec.getFlexVolume());
        } else if (pvSpec.getFlocker() != null) {
            info = printFlockerVolumeSource(pvSpec.getFlocker());
        }

        return info;
    }
}
