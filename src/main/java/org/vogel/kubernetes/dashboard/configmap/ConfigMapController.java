package org.vogel.kubernetes.dashboard.configmap;

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
@RequestMapping("/namespaces/{namespace}/configmaps")
public class ConfigMapController {

    private KubernetesUtils kubeUtils;

    public ConfigMapController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listConfigMaps(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listConfigMaps with namespace: {}", namespace);
        try {
            model.addAttribute("configMaps", kubeUtils.getConfigMaps(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "config_maps";
        } catch (ApiException e) {
            log.error("Error getting list of config maps", e);
            return "error";
        }
    }

    @GetMapping("/{configMapName}")
    public String describeConfigMap(Model model, @PathVariable("namespace") @NotNull String namespace,
                                    @PathVariable @NotNull String configMapName) {
        log.debug("In describeConfigMap with namespace: {} and config map: {}", namespace, configMapName);
        try {
            ConfigMap configMap = kubeUtils.getConfigMap(namespace, configMapName);
            model.addAttribute("configMap", configMap);
            model.addAttribute("configMapName", configMapName);
            model.addAttribute("events",
                               kubeUtils.getEvents(namespace, "ConfigMap", configMapName, configMap.getUid()));
            model.addAttribute("namespace", namespace);
            return "config_map_describe";
        } catch (ApiException e) {
            log.error("Error getting config maps", e);
            return "error";
        }
    }
}
