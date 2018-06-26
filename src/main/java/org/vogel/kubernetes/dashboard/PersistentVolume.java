package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.*;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class PersistentVolume {
    private String name;
    private String capacity;
    private String accessModes;
    private String reclaimPolicy;
    private String status;
    private String claim;
    private String storageClass;
    private String reason;
    private String age;

    public PersistentVolume(V1PersistentVolume pv) {
        V1ObjectMeta metadata = pv.getMetadata();
        V1PersistentVolumeSpec pvSpec = pv.getSpec();
        V1PersistentVolumeStatus pvStatus = pv.getStatus();

        name = metadata.getName();
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
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);
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

    private String getAccessModesAsString(List<String> accessModes) {
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
