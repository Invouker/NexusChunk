package eu.invouk.nexuschunk.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccesDeniedController {
    @GetMapping("/403")
    public String handle403(HttpServletRequest request, Model model) {
        return "error/access_denied_page";
    }

}
