package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@RequestMapping("/namespaces/{namespace}/replicasets")
public class ReplicaSetController {

    private KubernetesUtils kubeUtils;

    public ReplicaSetController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listReplicaSets(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listReplicaSets with namespace: {}", namespace);
        try {
            model.addAttribute("replicaSets", kubeUtils.getReplicaSets(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "replica_sets";
        } catch (ApiException e) {
            log.error("Error getting list of replica sets", e);
            return "error";
        }
    }

    @GetMapping("/{replicaSetName}")
    public String describeReplicaSet(Model model, @PathVariable("namespace") @NotNull String namespace,
                                     @PathVariable @NotNull String replicaSetName) {
        log.debug("In describeReplicaSet with namespace: {} and replica set: {}", namespace, replicaSetName);
        try {
            ReplicaSet replicaSet = kubeUtils.getReplicaSet(namespace, replicaSetName);
            model.addAttribute("replicaSet", replicaSet);
            model.addAttribute("replicaSetName", replicaSetName);
            model.addAttribute("events",
                               kubeUtils.getEvents(namespace, "ReplicaSet", replicaSetName, replicaSet.getUid()));
            model.addAttribute("namespace", namespace);
            return "replica_set_describe";
        } catch (ApiException e) {
            log.error("Error getting replica set", e);
            return "error";
        }
    }
}
