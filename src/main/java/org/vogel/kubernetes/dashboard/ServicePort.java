package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

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
        formatEndpoints(v1Endpoints, servicePort.getName());
    }

    private void formatEndpoints(V1Endpoints v1Endpoints, String name) {
        List<V1EndpointSubset> subsets = v1Endpoints.getSubsets();
        if (subsets.size() == 0) {
            endpoints = "<none>";
        }

        List<String> list = new ArrayList<>();
        for (V1EndpointSubset ss : subsets) {
            List<V1EndpointPort> ports = ss.getPorts();
            for (V1EndpointPort endpointPort : ports) {
                if (StringUtils.isEmpty(name) || name.equals(endpointPort.getName())) {
                    List<V1EndpointAddress> addresses = ss.getAddresses();
                    for (V1EndpointAddress address : addresses) {
                        String hostPort = String.format("%s:%s", address.getIp(), endpointPort.getPort()
                                .toString());
                        list.add(hostPort);
                    }
                }
            }
        }

        endpoints = list.stream()
                .collect(joining(","));
    }
}
