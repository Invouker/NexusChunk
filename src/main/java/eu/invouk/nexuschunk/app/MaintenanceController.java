package eu.invouk.nexuschunk.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaintenanceController {

    @GetMapping("/maintenance")
    public String showMaintenancePage() {
        return "maintenance"; // Odkaz na vašu Thymeleaf/HTML šablónu maintenance.html
    }
}