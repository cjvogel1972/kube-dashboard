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
@RequestMapping("/namespaces/{namespace}/persistentvolumeclaims")
public class PersistentVolumeClaimController {

    private KubernetesUtils kubeUtils;

    public PersistentVolumeClaimController(KubernetesUtils kubeUtils) {
        this.kubeUtils = kubeUtils;
    }

    @GetMapping
    public String listPersistentVolumeClaims(Model model, @PathVariable("namespace") String namespace) {
        log.debug("In listPersistentVolumeClaims with namespace: {}", namespace);
        try {
            model.addAttribute("persistentVolumeClaims", kubeUtils.getPersistentVolumeClaims(namespace));
            model.addAttribute("namespace", namespace);
            model.addAttribute("namespaces", kubeUtils.getNamespaces());
            return "persistent_volume_claims";
        } catch (ApiException e) {
            log.error("Error getting list of persistent volume claims", e);
            return "error";
        }
    }

    @GetMapping("/{persistentVolumeClaimName}")
    public String describePersistentVolumeClaim(Model model, @PathVariable("namespace") @NotNull String namespace,
                                                @PathVariable @NotNull String persistentVolumeClaimName) {
        log.debug("In describePersistentVolumeClaim with namespace: {} and persistent volume claim: {}", namespace,
                  persistentVolumeClaimName);
        try {
            PersistentVolumeClaim persistentVolumeClaim = kubeUtils.getPersistentVolumeClaim(namespace, persistentVolumeClaimName);
            model.addAttribute("persistentVolumeClaim", persistentVolumeClaim);
            model.addAttribute("persistentVolumeClaimName", persistentVolumeClaimName);
            model.addAttribute("events",
                               kubeUtils.getEvents(namespace, "PersistentVolumeClaim", persistentVolumeClaimName,
                                                   persistentVolumeClaim.getUid()));
            model.addAttribute("namespace", namespace);
            return "persistent_volume_claim_describe";
        } catch (ApiException e) {
            log.error("Error getting persistent volume", e);
            return "error";
        }
    }
}
