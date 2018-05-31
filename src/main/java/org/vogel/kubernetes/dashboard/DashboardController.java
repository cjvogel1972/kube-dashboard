package org.vogel.kubernetes.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @Value("${my.pod.namespace:default}")
    private String namespace;

    @GetMapping("/")
    public String index(Model model) {
        LOGGER.debug("In index with namespace: {}", namespace);
        model.addAttribute("namespace", namespace);
        return "index";
    }
}
