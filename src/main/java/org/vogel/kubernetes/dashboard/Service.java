package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class Service {
    private String name;
    private String svcType;
    private String clusterIp;
    private String externalIp;
    private String ports;
    private String age;

    public Service(V1Service service) {
        V1ObjectMeta metadata = service.getMetadata();
        V1ServiceSpec serviceSpec = service.getSpec();
        name = metadata.getName();
        svcType = serviceSpec.getType();
        clusterIp = serviceSpec.getClusterIP();
        if (StringUtils.isBlank(clusterIp)) {
            clusterIp = "<none>";
        }
        externalIp = getServiceExternalIP(service);
        ports = makePortString(serviceSpec.getPorts());
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);
    }

    private String getServiceExternalIP(V1Service service) {
        V1ServiceSpec spec = service.getSpec();
        if (StringUtils.equalsAny(svcType, "ClusterIP", "NodePort")) {
            List<String> externalIPs = spec.getExternalIPs();
            if (externalIPs != null && externalIPs.size() > 0) {
                return externalIPs.stream()
                        .collect(joining(","));
            }
            return "<none>";
        } else if (svcType.equals("LoadBalancer")) {
            String lbIps = loadBalancerStatusStringer(service.getStatus()
                                                              .getLoadBalancer());
            List<String> externalIPs = spec.getExternalIPs();
            if (externalIPs.size() > 0) {
                List<String> results = new ArrayList<>();
                results.add(lbIps);
                results.addAll(externalIPs);
                return results.stream()
                        .collect(joining(","));
            }

            if (StringUtils.isNotBlank(lbIps)) {
                return lbIps;
            }

            return "<pending>";
        } else if (svcType.equals("ExternalName")) {
            return spec.getExternalName();
        }

        return "<unknown>";
    }

    private String loadBalancerStatusStringer(V1LoadBalancerStatus loadBalancer) {
        List<V1LoadBalancerIngress> ingress = loadBalancer.getIngress();

        return ingress.stream()
                .map(this::ingressToString)
                .collect(joining(","));
    }

    private String ingressToString(V1LoadBalancerIngress ing) {
        if (StringUtils.isNotBlank(ing.getIp())) {
            return ing.getIp();
        } else if (StringUtils.isNotBlank(ing.getHostname())) {
            return ing.getHostname();
        }

        return null;
    }

    private String makePortString(List<V1ServicePort> ports) {
        if (ports.size() == 0) {
            return "<none>";
        } else {
            return ports.stream()
                    .map(this::portToString)
                    .collect(joining(","));
        }
    }

    private String portToString(V1ServicePort port) {
        if (port.getNodePort() != null && port.getNodePort() > 0) {
            return String.format("%d:%d/%s", port.getPort(), port.getNodePort(), port.getProtocol());
        } else {
            return String.format("%d/%s", port.getPort(), port.getProtocol());
        }
    }
}
