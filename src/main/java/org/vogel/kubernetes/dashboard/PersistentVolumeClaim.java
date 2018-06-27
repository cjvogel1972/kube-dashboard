package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.models.V1PersistentVolumeClaimStatus;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.vogel.kubernetes.dashboard.FormatUtils.*;

@Getter
public class PersistentVolumeClaim {
    private String name;
    private String status;
    private String volume;
    private String capacity;
    private String accessModes;
    private String storageClass;
    private String age;
    private String namespace;
    private String deletionTimestamp;
    private List<String> labels;
    private List<String> annotations;
    private String finalizers;
    private String volumeMode;
    private List<PersistentVolumeClaimCondition> conditions;
    private String uid;

    public PersistentVolumeClaim(V1PersistentVolumeClaim pvc) {
        V1ObjectMeta metadata = pvc.getMetadata();
        V1PersistentVolumeClaimSpec pvcSpec = pvc.getSpec();
        V1PersistentVolumeClaimStatus pvcStatus = pvc.getStatus();

        name = metadata.getName();
        if (metadata.getDeletionTimestamp() != null) {
            this.status = "Terminating";
        } else {
            this.status = pvcStatus.getPhase();
        }
        volume = pvcSpec.getVolumeName();
        capacity = "";
        accessModes = "";
        if (StringUtils.isNotEmpty(volume)) {
            accessModes = getAccessModesAsString(pvcSpec.getAccessModes());
            Quantity storage = pvcStatus.getCapacity()
                    .get("storage");
            capacity = storage.toSuffixedString();
        }
        storageClass = getPersistentVolumeClaimClass(pvc);
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);

        namespace = metadata.getNamespace();
        deletionTimestamp = translateTimestamp(metadata.getDeletionTimestamp());
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());
        if (metadata.getFinalizers() == null) {
            finalizers = "[]";
        } else {
            finalizers = metadata.getFinalizers()
                    .toString();
        }
        if (pvcStatus.getConditions() != null && pvcStatus.getConditions()
                .size() > 0) {
            conditions = pvcStatus.getConditions()
                    .stream()
                    .map(PersistentVolumeClaimCondition::new)
                    .collect(Collectors.toList());
        }
        uid = metadata.getUid();
    }

    private String getPersistentVolumeClaimClass(V1PersistentVolumeClaim pvc) {
        Map<String, String> annotations = pvc.getMetadata()
                .getAnnotations();
        if (annotations.containsKey("volume.beta.kubernetes.io/storage-class")) {
            return annotations.get("volume.beta.kubernetes.io/storage-class");
        }

        if (StringUtils.isNotEmpty(pvc.getSpec()
                                           .getStorageClassName())) {
            return pvc.getSpec()
                    .getStorageClassName();
        }

        return "";
    }
}
