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
@RequestMapping("/namespaces/{namespace}/persistentvolumes")
public class PersistentVolumeController {

    private KubernetesUtils kubeUtils;

    public PersistentVolumeController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listPersistentVolumes(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listPersistentVolumes with namespace: {}", namespace);
        try {
            model.addAttribute("persistentVolumes", kubeUtils.getPersistentVolumes());
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "persistent_volumes";
        } catch (ApiException e) {
            log.error("Error getting list of persistent volumes", e);
            return "error";
        }
    }
}
