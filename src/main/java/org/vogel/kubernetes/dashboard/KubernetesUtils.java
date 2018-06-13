package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta2Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public ReplicaSet getReplicaSet(String namespace, String replicaSetName) throws ApiException {
        AppsV1beta2Api api = new AppsV1beta2Api();

        V1beta2ReplicaSet kubeReplicatSet = api.readNamespacedReplicaSet(replicaSetName, namespace, null, null, null);
        ReplicaSet replicaSet = new ReplicaSet(kubeReplicatSet);
        PodStatus podStatus = getPodStatusForController(namespace, replicaSet.getSelector(),
                                                        kubeReplicatSet.getMetadata()
                                                                .getUid());
        replicaSet.setStatus(podStatus);
        return replicaSet;
    }

    public List<Event> getReplicaSetEvents(String namespace, String replicaSetName) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        String filter = String.format("involvedObject.name=%s", replicaSetName);
        V1EventList eventList = api.listNamespacedEvent(namespace, "false", null, filter, null, null, null, null, null,
                                                        null);

        return eventList.getItems()
                .stream()
                .map(Event::new)
                .collect(toList());
    }

    private PodStatus getPodStatusForController(String namespace, String selector, String uid) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        V1PodList podList = api.listNamespacedPod(namespace, "false", null, null, null, selector, null, null, null,
                                                  null);
        int running = 0;
        int waiting = 0;
        int succeeded = 0;
        int failed = 0;
        List<V1Pod> pods = podList.getItems();
        for (V1Pod pod : pods) {
            List<V1OwnerReference> ownerReferences = pod.getMetadata()
                    .getOwnerReferences();
            Optional<V1OwnerReference> ownerReference = ownerReferences.stream()
                    .filter(owner -> owner.isController() != null && Boolean.TRUE.equals(owner.isController()))
                    .findFirst();
            if (!ownerReference.isPresent() || !ownerReference.get()
                    .getUid()
                    .equals(uid)) {
                continue;
            }
            String phase = pod.getStatus()
                    .getPhase();
            if ("Running".equals(phase)) {
                running++;
            }
            if ("Pending".equals(phase)) {
                waiting++;
            }
            if ("Succeeded".equals(phase)) {
                succeeded++;
            }
            if ("Failed".equals(phase)) {
                failed++;
            }
        }

        return new PodStatus(running, waiting, succeeded, failed);
    }

    public List<Deployment> getDeployments(String namespace) throws ApiException {
        AppsV1beta2Api api = new AppsV1beta2Api();

        V1beta2DeploymentList deploymentList = api.listNamespacedDeployment(namespace, "false", null, null, null, null,
                                                                            null,
                                                                            null, null, null);
        return deploymentList.getItems()
                .stream()
                .map(Deployment::new)
                .collect(toList());
    }
}
