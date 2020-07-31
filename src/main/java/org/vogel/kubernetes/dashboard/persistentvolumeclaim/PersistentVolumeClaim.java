package org.vogel.kubernetes.dashboard.persistentvolumeclaim;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.models.V1PersistentVolumeClaimStatus;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.vogel.kubernetes.dashboard.Metadata;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.vogel.kubernetes.dashboard.FormatUtils.getAccessModesAsString;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class PersistentVolumeClaim extends Metadata {
    private String status;
    private String volume;
    private String capacity;
    private String accessModes;
    private String storageClass;
    private String deletionTimestamp;
    private String finalizers;
    private String volumeMode;
    private List<PersistentVolumeClaimCondition> conditions;

    public PersistentVolumeClaim(V1PersistentVolumeClaim pvc) {
        super(pvc.getMetadata());
        V1ObjectMeta metadata = pvc.getMetadata();
        V1PersistentVolumeClaimSpec pvcSpec = pvc.getSpec();
        V1PersistentVolumeClaimStatus pvcStatus = pvc.getStatus();

        if (metadata.getDeletionTimestamp() != null) {
            this.status = "Terminating";
        } else {
            this.status = pvcStatus.getPhase();
        }
        volume = pvcSpec.getVolumeName();
        capacity = "";
        accessModes = "";
        if (isNotEmpty(volume)) {
            accessModes = getAccessModesAsString(pvcSpec.getAccessModes());
            Map<String, Quantity> pvcStatusCapacity = pvcStatus.getCapacity();
            if (pvcStatusCapacity != null) {
                Quantity storage = pvcStatus.getCapacity()
                        .get("storage");
                capacity = storage.toSuffixedString();
            } else {
                capacity = "0";
            }
        }
        storageClass = getPersistentVolumeClaimClass(pvc);

        deletionTimestamp = translateTimestamp(metadata.getDeletionTimestamp());
        if (metadata.getFinalizers() == null) {
            finalizers = "[]";
        } else {
            finalizers = metadata.getFinalizers()
                    .toString();
        }
        if (CollectionUtils.isNotEmpty(pvcStatus.getConditions())) {
            conditions = pvcStatus.getConditions()
                    .stream()
                    .map(PersistentVolumeClaimCondition::new)
                    .collect(toList());
        }
    }

    private String getPersistentVolumeClaimClass(V1PersistentVolumeClaim pvc) {
        Map<String, String> annotations = pvc.getMetadata()
                .getAnnotations();
        if (annotations.containsKey("volume.beta.kubernetes.io/storage-class")) {
            return annotations.get("volume.beta.kubernetes.io/storage-class");
        }

        if (isNotEmpty(pvc.getSpec()
                               .getStorageClassName())) {
            return pvc.getSpec()
                    .getStorageClassName();
        }

        return "";
    }
}
