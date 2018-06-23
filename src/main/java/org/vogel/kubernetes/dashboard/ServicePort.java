package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1ServicePort;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class ServicePort {
    private String port;
    private String targetPort;
    private String nodePort;
    private String endpoints;

    public ServicePort(V1ServicePort servicePort, V1Endpoints v1Endpoints) {
        String name = servicePort.getName();
        if (StringUtils.isEmpty(name)) {
            name = "<unset>";
        }
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
        endpoints = FormatUtils.formatEndpoints(v1Endpoints, servicePort.getName());
    }
}
