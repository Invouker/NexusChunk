package eu.invouk.nexuschunk.app;

import eu.invouk.nexuschunk.app.settings.AppSettingsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminMaintenanceSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AppSettingsService appSettingsService;

    public AdminMaintenanceSuccessHandler(AppSettingsService appSettingsService) {
        this.appSettingsService = appSettingsService;
        setDefaultTargetUrl("/admin/dashboard"); // Kam má ísť Admin
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Je používateľ Admin?
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // 2. Je zapnutý režim údržby?
        boolean isMaintenance = appSettingsService.isMaintenanceMode();

        if (isMaintenance && !isAdmin) {
            // Ak je údržba a NEJDE O ADMINA:
            // Po úspešnom prihlásení ho presmerujeme na stránku údržby.
            getRedirectStrategy().sendRedirect(request, response, "/maintenance");
        } else {
            // Ak je ADMIN, alebo údržba nie je aktívna:
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}