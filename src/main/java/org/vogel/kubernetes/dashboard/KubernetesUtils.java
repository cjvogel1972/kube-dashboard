package org.vogel.kubernetes.dashboard;

import com.google.gson.Gson;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta2Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
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

    public List<Event> getEvents(String namespace, String kind, String replicaSetName, String uid) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        String filter = String.format("involvedObject.kind=%s,involvedObject.name=%s,involvedObject.uid=%s", kind,
                                      replicaSetName, uid);
        V1EventList eventList = api.listNamespacedEvent(namespace, "false", null, filter, null, null, null, null, null,
                                                        null);

        return eventList.getItems()
                .stream()
                .map(Event::new)
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
            if (!isControlledBy(pod.getMetadata(), uid)) {
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
                                                                            null, null, null, null);

        return deploymentList.getItems()
                .stream()
                .map(Deployment::new)
                .collect(toList());
    }

    public Deployment getDeployment(String namespace, String deploymentName) throws ApiException {
        AppsV1beta2Api api = new AppsV1beta2Api();

        V1beta2Deployment kubeDeployment = api.readNamespacedDeployment(deploymentName, namespace, null, null, null);
        Deployment deployment = new Deployment(kubeDeployment);
        List<V1beta2ReplicaSet> replicaSetList = getDeploymentReplicaSets(namespace, deployment.getSelector(),
                                                                          kubeDeployment.getMetadata()
                                                                                  .getUid());
        deployment.setOldReplicaSet(printReplicaSetsByLabels(findOldReplicaSet(kubeDeployment, replicaSetList)));
        Optional<V1beta2ReplicaSet> newReplicaSet = findNewReplicaSet(kubeDeployment, replicaSetList);
        List<V1beta2ReplicaSet> newReplicaSetList = new ArrayList<>();
        newReplicaSet.ifPresent(newReplicaSetList::add);
        deployment.setNewReplicaSet(printReplicaSetsByLabels(newReplicaSetList));

        return deployment;
    }

    private List<V1beta2ReplicaSet> getDeploymentReplicaSets(String namespace, String selector,
                                                             String uid) throws ApiException {
        AppsV1beta2Api api = new AppsV1beta2Api();

        V1beta2ReplicaSetList replicaSetList = api.listNamespacedReplicaSet(namespace, "false", null, null, null,
                                                                            selector, null, null, null, null);
        List<V1beta2ReplicaSet> replicaSets = replicaSetList.getItems();
        return replicaSets.stream()
                .filter(rs -> isControlledBy(rs.getMetadata(), uid))
                .collect(toList());
    }

    private boolean isControlledBy(V1ObjectMeta metadata, String uid) {
        Optional<V1OwnerReference> ownerReference = getControllerOf(metadata);
        return ownerReference.map(v1OwnerReference -> v1OwnerReference
                .getUid()
                .equals(uid))
                .orElse(false);
    }

    private Optional<V1OwnerReference> getControllerOf(V1ObjectMeta metadata) {
        return metadata.getOwnerReferences()
                .stream()
                .filter(owner -> owner.isController() != null && Boolean.TRUE.equals(owner.isController()))
                .findFirst();
    }

    private Optional<V1beta2ReplicaSet> findNewReplicaSet(V1beta2Deployment deployment,
                                                          List<V1beta2ReplicaSet> replicaSetList) {
        replicaSetList.sort((o1, o2) -> {
            V1ObjectMeta o1Metadata = o1.getMetadata();
            V1ObjectMeta o2Metadata = o2.getMetadata();
            if (o1Metadata.getCreationTimestamp()
                    .equals(o2Metadata.getCreationTimestamp())) {
                return o1Metadata.getName()
                        .compareTo(o2Metadata.getName());
            }
            return o1Metadata.getCreationTimestamp()
                    .compareTo(o2Metadata.getCreationTimestamp());
        });

        return replicaSetList.stream()
                .filter(rs -> equalIgnoreHash(rs.getSpec()
                                                      .getTemplate(), deployment.getSpec()
                                                      .getTemplate()))
                .findFirst();
    }

    private List<V1beta2ReplicaSet> findOldReplicaSet(V1beta2Deployment deployment,
                                                      List<V1beta2ReplicaSet> replicaSetList) {
        List<V1beta2ReplicaSet> requiredRSs = new ArrayList<>();
        Optional<V1beta2ReplicaSet> newReplicaSet = findNewReplicaSet(deployment, replicaSetList);
        for (V1beta2ReplicaSet rs : replicaSetList) {
            if (newReplicaSet.isPresent() && rs.getMetadata()
                    .getUid()
                    .equals(newReplicaSet.get()
                                    .getMetadata()
                                    .getUid())) {
                continue;
            }
            if (rs.getSpec()
                    .getReplicas() != 0) {
                requiredRSs.add(rs);
            }
        }

        return requiredRSs;
    }

    private boolean equalIgnoreHash(V1PodTemplateSpec template1, V1PodTemplateSpec template2) {
        Gson gson = new Gson();
        V1PodTemplateSpec t1Copy = gson.fromJson(gson.toJson(template1), V1PodTemplateSpec.class);
        V1PodTemplateSpec t2Copy = gson.fromJson(gson.toJson(template2), V1PodTemplateSpec.class);
        t1Copy.getMetadata()
                .getLabels()
                .remove("pod-template-hash");
        t2Copy.getMetadata()
                .getLabels()
                .remove("pod-template-hash");
        return t1Copy.equals(t2Copy);
    }

    private String printReplicaSetsByLabels(List<V1beta2ReplicaSet> replicaSets) {
        if (replicaSets.size() == 0) {
            return "<none>";
        } else {
            return replicaSets.stream()
                    .map(rs -> String.format("%s (%d/%d replicas created)", rs.getMetadata()
                            .getName(), rs.getStatus()
                                                     .getReplicas(), rs.getSpec()
                                                     .getReplicas()))
                    .collect(joining(","));
        }
    }

    public List<Service> getServices(String namespace) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        V1ServiceList serviceList = api.listNamespacedService(namespace, "false", null, null, null, null, null, null,
                                                              null, null);

        return serviceList.getItems()
                .stream()
                .map(Service::new)
                .collect(toList());
    }

    public Service getService(String namespace, String serviceName) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        V1Service kubeService = api.readNamespacedService(serviceName, namespace, null, null, null);
        V1EndpointsList endpointsList = getEndpoint(namespace, serviceName);
        V1Endpoints v1Endpoints = endpointsList.getItems()
                .get(0);

        return new Service(kubeService, v1Endpoints);
    }

    private V1EndpointsList getEndpoint(String namespace, String name) throws ApiException {
        CoreV1Api api = new CoreV1Api();

        String filter = String.format("metadata.name=%s", name);
        return api.listNamespacedEndpoints(namespace, "false", null, filter, null, null, null, null, null, null);
    }

    public List<Ingress> getIngresses(String namespace) throws ApiException {
        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();

        V1beta1IngressList ingressList = api.listNamespacedIngress(namespace, "false", null, null, null, null, null,
                                                                   null,
                                                                   null, null);

        return ingressList.getItems()
                .stream()
                .map(Ingress::new)
                .collect(toList());
    }
}