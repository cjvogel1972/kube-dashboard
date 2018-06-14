package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1LabelSelector;
import io.kubernetes.client.models.V1LabelSelectorRequirement;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class FormatUtils {
    public static String translateTimestamp(DateTime timestamp) {
        DateTime now = DateTime.now();
        Duration duration = new Duration(timestamp, now);
        return shortHumanDuration(duration);
    }

    public static List<String> printMultiline(Map<String, String> data) {
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

    public static String formatLabelSelector(@Nullable V1LabelSelector labelSelector) {
        String result;

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

        try {
            if (labelSelector == null) {
                result = "";
            } else if (matchLabelsSize + matchExpressionsSize == 0) {
                result = "";
            } else {
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
                        Requirement requirement = new Requirement(expression.getKey(), op, expression.getValues());
                        selector.add(requirement);
                    }
                }
                result = selector.string();
                if (result.length() == 0) {
                    result = "<none>";
                }
            }
        } catch (RequirementException e) {
            result = "<error>";
        }

        return result;
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
}
