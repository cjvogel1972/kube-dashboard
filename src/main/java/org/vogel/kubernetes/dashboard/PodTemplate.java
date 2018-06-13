package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodTemplateSpec;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Getter
public class PodTemplate {
    private List<String> labels;
    private List<String> annotations;
    private String serviceAccountName;
    private List<Container> initContainers;
    private List<Container> containers;
    private Volumes volumes;

    public PodTemplate(V1PodTemplateSpec template) {
        V1ObjectMeta metadata = template.getMetadata();
        labels = printMultiline(metadata.getLabels());

        if (metadata.getAnnotations() != null && metadata.getAnnotations()
                .size() > 0) {
            annotations = printMultiline(metadata.getAnnotations());
        }

        if (StringUtils.isNotEmpty(template.getSpec()
                                           .getServiceAccountName())) {
            serviceAccountName = template.getSpec()
                    .getServiceAccountName();
        }

        List<V1Container> kubeInitContainers = template.getSpec()
                .getInitContainers();
        if (kubeInitContainers != null) {
            this.initContainers = kubeInitContainers.stream()
                    .map(container -> new Container(container, null))
                    .collect(toList());
        }
        containers = template.getSpec()
                .getContainers()
                .stream()
                .map(container -> new Container(container, null))
                .collect(toList());

        volumes = new Volumes(template.getSpec()
                                      .getVolumes());
    }

    private List<String> printMultiline(Map<String, String> data) {
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
}
