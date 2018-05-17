package org.vogel.kubernetes.logreader;

import io.kubernetes.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Controller
public class LogsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsController.class);

    @Value("${my.pod.namespace:default}")
    private String namespace;

    @GetMapping("/pods")
    public String listPods(Model model) {
        LOGGER.debug("In listPods with namespace: {}", namespace);
        try {
            model.addAttribute("pods", KubernetesUtils.getPodsNames(namespace));
            model.addAttribute("namespace", namespace);
            return "pods";
        } catch (IOException | ApiException e) {
            LOGGER.error("Error getting list of pods", e);
            return "error";
        }
    }

    @GetMapping("/logs")
    public String showPodLogs(@RequestParam @NotNull String podName, Model model) {
        LOGGER.debug("In showPodLogs with namespace: {} and pod: {}", namespace, podName);
        try {
            String logs = KubernetesUtils.getLogs(namespace, podName);
            model.addAttribute("logs", logs);
            model.addAttribute("podName", podName);
            return "logs";
        } catch (IOException | ApiException e) {
            LOGGER.error("Error getting logs for pod {}", podName, e);
            return "error";
        }
    }
}
