package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.auth.services.CustomOAuth2UserService;
import eu.invouk.nexuschunk.auth.services.CustomOIDCUserService;
import eu.invouk.nexuschunk.auth.services.CustomUserDetailsService;
import eu.invouk.nexuschunk.user.permissions.EPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOIDCUserService customOIDCUserService;

    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService, CustomOAuth2UserService customOAuth2UserService, CustomOIDCUserService customOIDCUserService) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOIDCUserService = customOIDCUserService;
        log.info("Security configuration has been initialized");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // Admin panel: Prístup len pre užívateľov s rolou ADMIN
                        .requestMatchers("/admin/**").hasAuthority(EPermission.ADMIN_VIEW.getPermission())

                        // Používateľský panel: Prístup len pre prihlásených užívateľov (ADMIN aj USER)
                        //.requestMatchers("/dashboard").authenticated()

                        // Verejné URL: Prístupné pre kohokoľvek (aj neautentifikovaných)
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll() // static files
                        .requestMatchers("/resend_verification_code", "/verify", "/forgot-password", "/login", "/logout").permitAll()  // verification token
                        .requestMatchers("/profile/**").permitAll()

                        // Akékoľvek iné URL: Musia byť autentifikované (prihlásené)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/?modal=login")
                        .failureHandler(authenticationFailureHandler())
                        .loginProcessingUrl("/login")// Kde sa nachádza náš vlastný prihlasovací formulár
                        .defaultSuccessUrl("/", true) // Kam presmerovať po úspešnom prihlásení
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout") // URL pre odhlásenie
                        .logoutSuccessUrl("/") // Kam presmerovať po úspešnom odhlásení
                        .permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/");
                    oauth2.userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint
                                            // Pre OAuth2
                                            .userService(customOAuth2UserService)
                                            // 🔥 PRE OIDC (Google) 🔥
                                            .oidcUserService(customOIDCUserService))
                            .defaultSuccessUrl("/");
                })
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (_, response, authException) -> {

            Throwable cause = authException;
            if (authException instanceof InternalAuthenticationServiceException && authException.getCause() != null) {
                cause = authException.getCause();
            }

            switch (cause) {
                case LockedException lockedException -> response.sendRedirect("/?modal=login&error=locked_account");
                case DisabledException disabledException ->
                        response.sendRedirect("/?modal=login&error=disabled_account");
                case BadCredentialsException badCredentialsException ->
                        response.sendRedirect("/?modal=login&error=bad_credentials");
                case null, default -> response.sendRedirect("/?modal=login&error=" + authException.toString());
            }
        };
    }

}
