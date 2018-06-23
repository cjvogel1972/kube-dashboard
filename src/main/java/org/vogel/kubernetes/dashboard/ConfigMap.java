package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ObjectMeta;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.vogel.kubernetes.dashboard.FormatUtils.printMultiline;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class ConfigMap {
    private String name;
    private int dataSize;
    private String age;
    private String namespace;
    private List<String> labels;
    private List<String> annotations;
    private Map<String, String> data;
    private String uid;

    public ConfigMap(V1ConfigMap cm) {
        V1ObjectMeta metadata = cm.getMetadata();
        name = metadata.getName();
        data = cm.getData();
        if (data == null) {
            data = new HashMap<>();
        }
        dataSize = data.size();
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);

        namespace = metadata.getNamespace();
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());
        uid = metadata.getUid();
    }
}
