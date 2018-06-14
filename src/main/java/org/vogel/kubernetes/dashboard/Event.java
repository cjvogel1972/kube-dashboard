package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.models.V1Event;
import io.kubernetes.client.models.V1EventSource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.vogel.kubernetes.dashboard.FormatUtils.translateTimestamp;

@Getter
public class Event {
    private String type;
    private String reason;
    private String interval;
    private String source;
    private String message;

    public Event(V1Event event) {
        type = event.getType();
        reason = event.getReason();
        if (event.getCount() > 1) {
            interval = String.format("%s (x%d over %s)", translateTimestamp(event.getLastTimestamp()), event.getCount(),
                                     translateTimestamp(event.getFirstTimestamp()));
        } else {
            interval = translateTimestamp(event.getFirstTimestamp());
        }
        source = formatEventSource(event.getSource());
        message = event.getMessage();
    }

    private String formatEventSource(V1EventSource source) {
        List<String> eventSourceString = new ArrayList<>();
        eventSourceString.add(source.getComponent());
        if (isNotBlank(source.getHost())) {
            eventSourceString.add(source.getHost());
        }

        return eventSourceString.stream()
                .collect(joining(","));
    }
}
