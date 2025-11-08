package eu.invouk.nexuschunk.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

@Controller
@RequestMapping("/error")
public class Error implements ErrorController {

    @GetMapping
    public String handleError(HttpServletRequest request, Model model) {

        // Získanie kódu stavu (napr. 404, 500)
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Získanie samotnej výnimky (Throwable/Exception)
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String errorMessage = "Neznáma chyba";
        String errorDetails = "";
        int statusCode = 500;

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        if (throwable != null) {
            // Správa k výnimke
            errorMessage = throwable.getMessage();

            // Stack Trace (užitočné pre DEBUG, ale nezobrazujte voči klientovi!)
            errorDetails = getStackTrace(throwable);
        }

        // Pridanie informácií do modelu
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorDetails", errorDetails); // Používajte len pre adminov/debug

        // Vrátenie názvu šablóny
        return "error";
    }

    // Pomocná metóda na získanie Stack Trace ako String
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
