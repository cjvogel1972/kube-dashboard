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
@RequestMapping("/namespaces/{namespace}/deployments")
public class DeploymentController {

    private KubernetesUtils kubeUtils;

    public DeploymentController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listDeployments(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listDeployments with namespace: {}", namespace);
        try {
            model.addAttribute("deployments", kubeUtils.getDeployments(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "deployments";
        } catch (ApiException e) {
            log.error("Error getting list of deployments", e);
            return "error";
        }
    }
}
