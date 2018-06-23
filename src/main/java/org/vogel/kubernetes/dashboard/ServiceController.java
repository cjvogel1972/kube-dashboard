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

    @GetMapping("/{serviceName}")
    public String describeService(Model model, @PathVariable("namespace") @NotNull String namespace,
                                  @PathVariable @NotNull String serviceName) {
        log.debug("In describePod with namespace: {} and pod: {}", namespace, serviceName);
        try {
            Service service = kubeUtils.getService(namespace, serviceName);
            model.addAttribute("service", service);
            model.addAttribute("serviceName", serviceName);
            model.addAttribute("events", kubeUtils.getEvents(namespace, "Service", serviceName, service.getUid()));
            model.addAttribute("namespace", namespace);
            return "service_describe";
        } catch (ApiException e) {
            log.error("Error getting service", e);
            return "error";
        }
    }
}
