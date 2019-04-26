package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsAny;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FormatUtils {
    public static String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return shortHumanDuration(duration);
    }

    private static String shortHumanDuration(Duration d) {
        String result;

        if (d.getStandardDays() > 365) {
            result = format("%dy", d.getStandardDays() / 365);
        } else if (d.getStandardDays() > 0) {
            result = format("%dd", d.getStandardDays());
        } else if (d.getStandardHours() > 0) {
            result = format("%dh", d.getStandardHours());
        } else if (d.getStandardMinutes() > 0) {
            result = format("%dm", d.getStandardMinutes());
        } else {
            result = format("%ds", d.getStandardSeconds());
        }

        return result;
    }

    public static List<String> printMultiline(Map<String, String> data) {
        List<String> result = null;

        if (MapUtils.isNotEmpty(data)) {
            result = data.keySet()
                    .stream()
                    .sorted()
                    .map(key -> String.format("%s=%s", key, data.get(key)))
                    .collect(toList());
        }

        return result;
    }

    public static String formatLabelSelector(@Nullable V1LabelSelector labelSelector) {
        int matchLabelsSize = 0;
        int matchExpressionsSize = 0;
        if (labelSelector != null) {
            if (labelSelector.getMatchLabels() != null) {
                matchLabelsSize = labelSelector.getMatchLabels()
                        .size();
            }
            if (labelSelector.getMatchExpressions() != null) {
                matchExpressionsSize = labelSelector.getMatchExpressions()
                        .size();
            }
        }

        String result;
        try {
            if (labelSelector == null) {
                result = "";
            } else if (matchLabelsSize + matchExpressionsSize == 0) {
                result = "";
            } else {
                result = processLabelSelector(labelSelector);
            }
        } catch (RequirementException e) {
            result = "<error>";
        }

        return result;
    }

    private static String processLabelSelector(@NotNull V1LabelSelector labelSelector) throws RequirementException {
        Selector selector = new Selector();
        if (labelSelector.getMatchLabels() != null) {
            for (Map.Entry<String, String> entry : labelSelector.getMatchLabels()
                    .entrySet()) {
                List<String> values = Collections.singletonList(entry.getValue());
                Requirement requirement = new Requirement(entry.getKey(), "=", values);
                selector.add(requirement);
            }
        }
        if (labelSelector.getMatchExpressions() != null) {
            for (V1LabelSelectorRequirement expression : labelSelector.getMatchExpressions()) {
                String op = convertOperatorValue(expression);
                Requirement requirement = new Requirement(expression.getKey(), op, expression.getValues());
                selector.add(requirement);
            }
        }

        return StringUtils.defaultIfBlank(selector.string(), "<none>");
    }

    private static String convertOperatorValue(V1LabelSelectorRequirement expression) throws RequirementException {
        String op;
        switch (expression.getOperator()) {
            case "In":
                op = "in";
                break;
            case "NotIn":
                op = "notin";
                break;
            case "Exists":
                op = "exists";
                break;
            case "DoesNotExist":
                op = "!";
                break;
            default:
                String msg = String.format("%s is not a valid pod selector operator",
                                           expression.getOperator());
                throw new RequirementException(msg);
        }
        return op;
    }

    public static String describeBackend(String namespace, String serviceName, String servicePort,
                                         KubernetesUtils kubernetesUtils) throws ApiException {
        V1EndpointsList endpointsList = kubernetesUtils.getEndpoint(namespace, serviceName);
        V1Endpoints v1Endpoints = null;
        if (!endpointsList.getItems()
                .isEmpty()) {
            v1Endpoints = endpointsList.getItems()
                    .get(0);
        }
        String spName = "";
        try {
            V1Service service = kubernetesUtils.getKubeService(namespace, serviceName);
            List<V1ServicePort> ports = service.getSpec()
                    .getPorts();
            for (V1ServicePort port : ports) {
                if (equalsAny(servicePort, port.getName(), port.getPort()
                        .toString())) {
                    spName = port.getName();
                }
            }
        } catch (ApiException e) {
//            e.printStackTrace();
        }

        return formatEndpoints(v1Endpoints, spName);
    }

    public static String formatEndpoints(V1Endpoints v1Endpoints, String name) {
        if (v1Endpoints == null) {
            return "<none>";
        }

        List<V1EndpointSubset> subsets = v1Endpoints.getSubsets();
        if (subsets.isEmpty()) {
            return "<none>";
        }

        List<String> list = new ArrayList<>();
        for (V1EndpointSubset ss : subsets) {
            List<V1EndpointPort> ports = ss.getPorts();
            for (V1EndpointPort endpointPort : ports) {
                if (isEmpty(name) || name.equals(endpointPort.getName())) {
                    List<V1EndpointAddress> addresses = ss.getAddresses();
                    for (V1EndpointAddress address : addresses) {
                        String hostPort = String.format("%s:%s", address.getIp(), endpointPort.getPort()
                                .toString());
                        list.add(hostPort);
                    }
                }
            }
        }

        return joinListWithCommas(list);
    }

    public static String getAccessModesAsString(List<String> accessModes) {
        Set<String> modes = new HashSet<>(accessModes);
        List<String> modesStr = new ArrayList<>();
        if (modes.contains("ReadWriteOnce")) {
            modesStr.add("RWO");
        }
        if (modes.contains("ReadOnlyMany")) {
            modesStr.add("ROX");
        }
        if (modes.contains("ReadWriteMany")) {
            modesStr.add("RWX");
        }

        return joinListWithCommas(modesStr);
    }

    public static String joinListWithCommas(List<String> list) {
        return String.join(",", list);
    }
}
