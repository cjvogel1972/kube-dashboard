package org.vogel.kubernetes.dashboard.persistentvolume;

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

    @GetMapping("/{persistentVolumeName}")
    public String describePersistentVolume(Model model, @PathVariable("namespace") @NotNull String namespace,
                                           @PathVariable @NotNull String persistentVolumeName) {
        log.debug("In describePersistentVolume with namespace: {} and persistent volume: {}", namespace,
                  persistentVolumeName);
        try {
            PersistentVolume persistentVolume = kubeUtils.getPersistentVolume(persistentVolumeName);
            model.addAttribute("persistentVolume", persistentVolume);
            model.addAttribute("persistentVolumeName", persistentVolumeName);
            model.addAttribute("events",
                               kubeUtils.getEvents(namespace, "PersistentVolume", persistentVolumeName,
                                                   persistentVolume.getUid()));
            model.addAttribute("namespace", namespace);
            return "persistent_volume_describe";
        } catch (ApiException e) {
            log.error("Error getting persistent volume", e);
            return "error";
        }
    }
}
