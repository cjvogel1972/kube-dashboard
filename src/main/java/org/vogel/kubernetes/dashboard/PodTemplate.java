package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodTemplateSpec;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.vogel.kubernetes.dashboard.FormatUtils.printMultiline;

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
        annotations = printMultiline(metadata.getAnnotations());

        if (isNotEmpty(template.getSpec()
                               .getServiceAccountName())) {
            serviceAccountName = template.getSpec()
                    .getServiceAccountName();
        }

        List<V1Container> kubeInitContainers = template.getSpec()
                .getInitContainers();
        if (kubeInitContainers != null) {
            initContainers = kubeInitContainers.stream()
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
}
