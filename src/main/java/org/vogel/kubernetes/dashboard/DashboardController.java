package org.vogel.kubernetes.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class DashboardController {

    @Value("${my.pod.defaultNamespace:default}")
    private String defaultNamespace;

    @GetMapping("/")
    public String index(Model model) {
        log.debug("In index with defaultNamespace: {}", defaultNamespace);
        model.addAttribute("namespace", defaultNamespace);
        return "index";
    }

    @GetMapping("/namespaces/{namespace}")
    public String namespace(Model model, @PathVariable String namespace) {
        log.debug("In namespace with namespace: {}", namespace);
        model.addAttribute("namespace", defaultNamespace);
        return "index";
    }
}
