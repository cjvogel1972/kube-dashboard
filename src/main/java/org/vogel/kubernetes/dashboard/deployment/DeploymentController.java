package org.vogel.kubernetes.dashboard.deployment;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vogel.kubernetes.dashboard.KubernetesUtils;

import javax.validation.constraints.NotNull;

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

    @GetMapping("/{deploymentName}")
    public String describeDeployment(Model model, @PathVariable("namespace") @NotNull String namespace,
                                     @PathVariable @NotNull String deploymentName) {
        log.debug("In describeDeployment with namespace: {} and deployment: {}", namespace, deploymentName);
        try {
            Deployment deployment = kubeUtils.getDeployment(namespace, deploymentName);
            model.addAttribute("deployment", deployment);
            model.addAttribute("deploymentName", deploymentName);
            model.addAttribute("events",
                               kubeUtils.getEvents(namespace, "Deployment", deploymentName, deployment.getUid()));
            model.addAttribute("namespace", namespace);
            return "deployment_describe";
        } catch (ApiException e) {
            log.error("Error getting deployment", e);
            return "error";
        }
    }
}
