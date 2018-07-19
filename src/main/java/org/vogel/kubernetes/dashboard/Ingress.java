package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.vogel.kubernetes.dashboard.FormatUtils.describeBackend;

@Getter
public class Ingress extends Metadata {
    private String hosts;
    private String addresses;
    private String ports;
    private String defaultBackend;
    private List<String> tls;
    private List<IngressRule> rules;

    public Ingress(V1beta1Ingress ingress) {
        super(ingress.getMetadata());
        V1beta1IngressSpec ingressSpec = ingress.getSpec();
        V1beta1IngressStatus ingressStatus = ingress.getStatus();
        List<V1beta1IngressRule> ingressRules = ingressSpec.getRules();
        hosts = formatHosts(ingressRules);
        addresses = loadBalancerStatusStringer(ingressStatus.getLoadBalancer());
        List<V1beta1IngressTLS> specTls = ingressSpec.getTls();
        ports = formatPorts(specTls);

        if (specTls != null && specTls.size() != 0) {
            describeIngressTLS(specTls);
        }
    }

    public Ingress(V1beta1Ingress ingress, KubernetesUtils kubernetesUtils) throws ApiException {
        this(ingress);
        V1beta1IngressSpec ingressSpec = ingress.getSpec();
        V1beta1IngressBackend backend = ingressSpec.getBackend();
        String serviceName;
        String servicePort;
        String ns;
        if (backend == null) {
            serviceName = "default-http-backend";
            servicePort = "80";
            ns = "kube-system";
        } else {
            serviceName = backend.getServiceName();
            IntOrString backendServicePort = backend.getServicePort();
            if (backendServicePort.isInteger()) {
                servicePort = backendServicePort.getIntValue()
                        .toString();
            } else {
                servicePort = backendServicePort.getStrValue();
            }
            ns = getNamespace();
        }
        String describeBackend = describeBackend(ns, serviceName, servicePort, kubernetesUtils);
        defaultBackend = String.format("%s:%s (%s)", serviceName, servicePort, describeBackend);
        List<V1beta1IngressRule> ingressRules = ingressSpec.getRules();
        rules = new ArrayList<>();
        for (V1beta1IngressRule rule : ingressRules) {
            if (rule.getHttp() == null) {
                continue;
            }
            rules.add(new IngressRule(rule, ns, kubernetesUtils));
        }
    }

    private String formatHosts(List<V1beta1IngressRule> rules) {
        List<String> list = rules.stream()
                .filter(rule -> isNotEmpty(rule.getHost()))
                .map(V1beta1IngressRule::getHost)
                .collect(toList());

        if (list.size() == 0) {
            return "*";
        } else {
            return list.stream()
                    .collect(joining(","));
        }
    }

    private String loadBalancerStatusStringer(V1LoadBalancerStatus loadBalancer) {
        List<V1LoadBalancerIngress> ingress = loadBalancer.getIngress();

        if (ingress == null) {
            return null;
        }

        return ingress.stream()
                .map(this::ingressToString)
                .collect(joining(","));
    }

    private String ingressToString(V1LoadBalancerIngress ing) {
        if (isNotBlank(ing.getIp())) {
            return ing.getIp();
        } else if (isNotBlank(ing.getHostname())) {
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

    private void describeIngressTLS(List<V1beta1IngressTLS> specTls) {
        tls = new ArrayList<>();
        for (V1beta1IngressTLS t : specTls) {
            String tlsHosts = t.getHosts()
                    .stream()
                    .collect(joining(","));
            if (isEmpty(t.getSecretName())) {
                tls.add(String.format("SNI routes %s", tlsHosts));
            } else {
                tls.add(String.format("%s terminates %s", t.getSecretName(), tlsHosts));
            }
        }
    }
}
