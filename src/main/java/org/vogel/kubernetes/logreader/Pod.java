package org.vogel.kubernetes.logreader;

import io.kubernetes.client.models.V1Pod;

public class Pod {
    private String name;
    private String state;
    private String image;

    public Pod(V1Pod pod) {
        name = pod.getMetadata().getName();
        state = pod.getStatus().getPhase();
        image = pod.getSpec().getContainers().get(0).getImage();
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getImage() {
        return image;
    }
}
