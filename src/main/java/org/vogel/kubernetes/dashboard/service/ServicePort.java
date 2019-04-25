package org.vogel.kubernetes.dashboard.service;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1ServicePort;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.vogel.kubernetes.dashboard.FormatUtils.formatEndpoints;

@Getter
public class ServicePort {
    private String port;
    private String targetPort;
    private String nodePort;
    private String endpoints;

    public ServicePort(V1ServicePort servicePort, V1Endpoints v1Endpoints) {
        String name = defaultIfBlank(servicePort.getName(), "<unset>");
        String protocol = servicePort.getProtocol();
        port = String.format("%s %d/%s", name, servicePort.getPort(), protocol);
        IntOrString targetPort = servicePort.getTargetPort();
        if (targetPort.isInteger()) {
            this.targetPort = String.format("%d/%s", targetPort.getIntValue(), protocol);
        } else {
            this.targetPort = String.format("%s/%s", targetPort.getStrValue(), protocol);
        }
        if (servicePort.getNodePort() != null && servicePort.getNodePort() != 0) {
            nodePort = String.format("%s %d/%s", name, servicePort.getNodePort(), protocol);
        }
        endpoints = formatEndpoints(v1Endpoints, servicePort.getName());
    }
}
