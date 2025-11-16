package eu.invouk.nexuschunk.app;

import eu.invouk.nexuschunk.app.settings.AppSettingsService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@Configuration
public class MaintenanceFilter implements Filter {

    private final AppSettingsService appSettingsService;

    public MaintenanceFilter(AppSettingsService appSettingsService) {
        this.appSettingsService = appSettingsService;
    }

    private final String MAINTENANCE_URL = "/maintenance";
    private final String LOGIN_ENDPOINT = "/login"; // Endpoint, kam sa posiela POST

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (appSettingsService.isMaintenanceMode()) {

            String requestUri = httpRequest.getRequestURI();
            String method = httpRequest.getMethod(); // Získanie metódy (GET, POST, atď.)

            // 1. Povolené výnimky (Statika, Stránka údržby a Hlavná stránka /)
            boolean isAllowedUrl = requestUri.startsWith(MAINTENANCE_URL)
                    || requestUri.startsWith("/css/")
                    || requestUri.startsWith("/js/")
                    || requestUri.equals("/"); // 🔑 Povolíme celú hlavnú stránku

            // 2. Povolenie POST požiadavky na prihlásenie
            boolean isLoginAttempt = requestUri.equals(LOGIN_ENDPOINT) && method.equalsIgnoreCase("POST");


            // 4. Kontrola: Je používateľ už prihlásený A nie je to prihlasovacia stránka?
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated();

            // Ak je to povolená URL (vrátane /), alebo je to pokus o prihlásenie, alebo je už prihlásený Admin
            if (isAllowedUrl || isLoginAttempt || isAuthenticated) {

                // POZOR: Pre neautentifikovaných musíme vrátiť 503, nie len pokračovať!
                // Ak je údržba, ale nie si prihlásený a nie si Admin, môžeš vidieť len ten modal.

                // Ak je to hlavná stránka ("/") a nie je to prihlásený Admin, nastavíme 503
                if (requestUri.equals("/") && !isAuthenticated) {
                    httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    // Nastavíme 503, ale necháme to spracovať, aby sa zobrazil HTML/modal
                }

                chain.doFilter(request, response);
                return;
            }

            // 5. Blokovať a presmerovať
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            httpResponse.setHeader("Retry-After", "3600");
            httpResponse.sendRedirect(httpRequest.getContextPath() + MAINTENANCE_URL);
            return;
        }

        chain.doFilter(request, response);
    }
}
