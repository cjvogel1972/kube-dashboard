package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ConfigMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigMap extends Metadata {
    private int dataSize;
    private Map<String, String> data;

    public ConfigMap(V1ConfigMap cm) {
        super(cm.getMetadata());
        data = cm.getData();
        if (data == null) {
            data = new HashMap<>();
        }
        dataSize = data.size();
    }
}
