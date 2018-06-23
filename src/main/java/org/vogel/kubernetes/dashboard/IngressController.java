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
@RequestMapping("/namespaces/{namespace}/ingresses")
public class IngressController {

    private KubernetesUtils kubeUtils;

    public IngressController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listIngresses(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listIngresses with namespace: {}", namespace);
        try {
            model.addAttribute("ingresses", kubeUtils.getIngresses(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "ingresses";
        } catch (ApiException e) {
            log.error("Error getting list of ingresses", e);
            return "error";
        }
    }
}
