package org.vogel.kubernetes.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DashboardController {

    @Value("${my.pod.namespace:default}")
    private String namespace;

    @GetMapping("/")
    public String index(Model model) {
        log.debug("In index with namespace: {}", namespace);
        model.addAttribute("namespace", namespace);
        return "index";
    }
}
