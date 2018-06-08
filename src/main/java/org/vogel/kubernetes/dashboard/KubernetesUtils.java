package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta2Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1EventList;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1beta2ReplicaSetList;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class KubernetesUtils {

    public KubernetesUtils() throws IOException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
    }

    public List<String> getNamespaces() throws ApiException {
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null);

        return namespaces.getItems()
                .stream()
                .map(ns -> ns.getMetadata()
                        .getName())
                .collect(toList());
    }

    public List<Pod> getPods(String namespace) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);

        return list.getItems()
                .stream()
                .map(Pod::new)
                .collect(toList());
    }

    public Pod getPod(String namespace, String podName) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        return new Pod(api.readNamespacedPod(podName, namespace, null, null, null));
    }

    public String getPodLogs(String namespace, String podName) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        return api.readNamespacedPodLog(podName, namespace, null, null, null, "false", null, null, null, null);
    }

    public List<Event> getPodEvents(String namespace, String podName) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        String filter = String.format("involvedObject.name=%s", podName);
        V1EventList eventList = api.listNamespacedEvent(namespace, "false", null, filter, null, null, null, null, null,
                                                        null);

        return eventList.getItems()
                .stream()
                .map(Event::new)
                .collect(toList());
    }

    public List<ReplicaSet> getReplicaSets(String namespace) throws ApiException {
        AppsV1beta2Api api = new AppsV1beta2Api();

        V1beta2ReplicaSetList list = api.listNamespacedReplicaSet(namespace, "false", null, null, null, null, null,
                                                                  null, null, null);

        return list.getItems()
                .stream()
                .map(ReplicaSet::new)
                .collect(toList());
    }
}
