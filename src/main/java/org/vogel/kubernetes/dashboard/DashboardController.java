package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class DashboardController {

    private String defaultNamespace;

    private KubernetesUtils kubernetesUtils;

    public DashboardController(KubernetesUtils kubernetesUtils,
                               @Value("${my.pod.namespace:default}") String defaultNamespace) {
        this.kubernetesUtils = kubernetesUtils;
        this.defaultNamespace = defaultNamespace;
    }

    @GetMapping("/")
    public String index(Model model) {
        log.debug("In index with defaultNamespace: {}", defaultNamespace);
        return namespace(model, defaultNamespace);
    }

    @GetMapping("/namespaces/{namespace}")
    public String namespace(Model model, @PathVariable String namespace) {
        log.debug("In namespace with namespace: {}", namespace);
        try {
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubernetesUtils.getNamespaces());
        } catch (ApiException e) {
            log.error("Error getting list of namespaces", e);
            return "error";
        }
        return "index";
    }
}
