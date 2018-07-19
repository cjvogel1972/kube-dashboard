package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1ObjectMeta;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.List;

import static org.vogel.kubernetes.dashboard.FormatUtils.printMultiline;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class Metadata {
    private String name;
    private String namespace;
    private List<String> labels;
    private List<String> annotations;
    private DateTime creationTimestamp;
    private String age;
    private String uid;

    protected Metadata(V1ObjectMeta metadata) {
        name = metadata.getName();
        namespace = metadata.getNamespace();
        labels = printMultiline(metadata.getLabels());
        annotations = printMultiline(metadata.getAnnotations());
        creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);
        uid = metadata.getUid();
    }
}
