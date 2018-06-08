package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
