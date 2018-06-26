package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1Volume;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.vogel.kubernetes.dashboard.FormatUtils.*;

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
}
