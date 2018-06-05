package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1EventList;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class KubernetesUtils {
    public static List<String> getNamespaces() throws ApiException, IOException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1NamespaceList namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null);
        return namespaces.getItems()
                .stream()
                .map(ns -> ns.getMetadata()
                        .getName())
                .collect(toList());
    }

    public static List<Pod> getPods(String namespace) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);

        return list.getItems()
                .stream()
                .map(Pod::new)
                .collect(toList());
    }

    public static Pod getPod(String namespace, String podName) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        return new Pod(api.readNamespacedPod(podName, namespace, null, null, null));
    }

    public static String getPodLogs(String namespace, String podName) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        return api.readNamespacedPodLog(podName, namespace, null, null, null, "false", null, null, null, null);
    }

    public static List<Event> getPodEvents(String namespace, String podName) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        String filter = String.format("involvedObject.name=%s", podName);
        V1EventList eventList = api.listNamespacedEvent(namespace, "false", null, filter, null, null, null, null, null,
                                                        null);

        return eventList.getItems()
                .stream()
                .map(Event::new)
                .collect(toList());
    }
}
