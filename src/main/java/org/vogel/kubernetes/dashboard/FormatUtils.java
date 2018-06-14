package org.vogel.kubernetes.dashboard;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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
