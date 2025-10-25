package eu.invouk.nexuschunk.auth.listeners;

import eu.invouk.nexuschunk.auth.services.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
        String userName = event.getAuthentication().getName();
        loginAttemptService.loginSuccess(userName);
    }

}
