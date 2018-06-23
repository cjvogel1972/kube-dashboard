package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class Ingress {
    private String name;
    private String hosts;
    private String addresses;
    private String ports;
    private String age;

    public Ingress(V1beta1Ingress ingress) {
        V1ObjectMeta metadata = ingress.getMetadata();
        V1beta1IngressSpec ingressSpec = ingress.getSpec();
        V1beta1IngressStatus ingressStatus = ingress.getStatus();
        name = metadata.getName();
        hosts = formatHosts(ingressSpec.getRules());
        addresses = loadBalancerStatusStringer(ingressStatus.getLoadBalancer());
        ports = formatPorts(ingressSpec.getTls());
        DateTime creationTimestamp = metadata.getCreationTimestamp();
        age = translateTimestamp(creationTimestamp);
    }

    private String formatHosts(List<V1beta1IngressRule> rules) {
        List<String> list = rules.stream()
                .filter(rule -> StringUtils.isNotEmpty(rule.getHost()))
                .map(V1beta1IngressRule::getHost)
                .collect(Collectors.toList());

        if (list.size() == 0) {
            return "*";
        } else {
            return list.stream()
                    .collect(joining(","));
        }
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

    private String formatPorts(List<V1beta1IngressTLS> tls) {
        if (tls != null && tls.size() != 0) {
            return "80, 443";
        } else {
            return "80";
        }
    }
}
