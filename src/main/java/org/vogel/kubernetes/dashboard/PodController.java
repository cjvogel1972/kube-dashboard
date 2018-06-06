package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@RequestMapping("/namespaces/{namespace}")
public class PodController {

    @Value("${my.pod.defaultNamespace:default}")
    private String defaultNamespace;

    private KubernetesUtils kubeUtils;

    public PodController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping("/pods")
    public String listPods(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listPods with namespace: {}", namespace);
        try {
            model.addAttribute("pods", kubeUtils.getPods(namespace));
            model.addAttribute("namespace", namespace);
            return "pods";
        } catch (ApiException e) {
            log.error("Error getting list of pods", e);
            return "error";
        }
    }

    @GetMapping("/pods/{podName}")
    public String describePod(Model model, @PathVariable("namespace") @NotNull String namespace,
                              @PathVariable @NotNull String podName) {
        log.debug("In describePod with namespace: {} and pod: {}", namespace, podName);
        try {
            model.addAttribute("pod", kubeUtils.getPod(namespace, podName));
            model.addAttribute("podName", podName);
            model.addAttribute("events", kubeUtils.getPodEvents(namespace, podName));
            model.addAttribute("namespace", namespace);
            return "pod_describe";
        } catch (ApiException e) {
            log.error("Error getting pod", e);
            return "error";
        }
    }

    @GetMapping("/pods/{podName}/logs")
    public String showPodLogs(Model model, @PathVariable("namespace") @NotNull String namespace,
                              @PathVariable @NotNull String podName) {
        log.debug("In showPodLogs with namespace: {} and pod: {}", namespace, podName);
        try {
            String logs = kubeUtils.getPodLogs(namespace, podName);
            model.addAttribute("logs", logs);
            model.addAttribute("podName", podName);
            model.addAttribute("namespace", namespace);
            return "logs";
        } catch (ApiException e) {
            log.error("Error getting logs for pod {}", podName, e);
            return "error";
        }
    }
}
