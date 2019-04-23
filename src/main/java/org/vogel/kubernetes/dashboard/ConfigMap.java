package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ConfigMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Getter
public class ConfigMap extends Metadata {
    private int dataSize;
    private Map<String, String> data;

    public ConfigMap(V1ConfigMap cm) {
        super(cm.getMetadata());
        data = defaultIfNull(cm.getData(), new HashMap<>());
        dataSize = data.size();
    }
}
