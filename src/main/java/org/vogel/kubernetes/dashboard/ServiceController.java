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
@RequestMapping("/namespaces/{namespace}/services")
public class ServiceController {

    private KubernetesUtils kubeUtils;

    public ServiceController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listServices(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listServices with namespace: {}", namespace);
        try {
            model.addAttribute("services", kubeUtils.getServices(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "services";
        } catch (ApiException e) {
            log.error("Error getting list of services", e);
            return "error";
        }
    }
}
