package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.auth.services.CustomOAuth2UserService;
import eu.invouk.nexuschunk.auth.services.CustomOIDCUserService;
import eu.invouk.nexuschunk.auth.services.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

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

    private static final String LIVE_RELOAD_CSP =
            "default-src 'self';" +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://kit.fontawesome.com https://www.google.com https://www.gstatic.com http://localhost:35729 http://localhost:*;" +
                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net;" +

                    "img-src 'self' data: https: https://vzge.me;" +
                    "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net https://ka-f.fontawesome.com;" +
                    "frame-src 'self' https://www.google.com;" +
                    "worker-src 'self' blob:;" +
                    "connect-src 'self' ws://localhost:35729 http://localhost:35729 https://cdn.jsdelivr.net https://ka-f.fontawesome.com;";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {

        http.headers(headers -> headers
                        // Odstránenie všetkých predvolených hlavičiek
                        .defaultsDisabled()

                        // 1. CSP (používame LIVE_RELOAD_CSP, ktorá musí obsahovať povolenia pre tsParticles a LiveReload)
                        .addHeaderWriter(new ContentSecurityPolicyHeaderWriter(LIVE_RELOAD_CSP))

                        // 2. X-Frame-Options
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))

                // Prípadne iné hlavičky, ktoré chcete zachovať...
        );

        http
                .authorizeHttpRequests((requests) -> requests
                        // Admin panel: Prístup len pre užívateľov s rolou ADMIN
                        .requestMatchers("/admin/**").permitAll()//.hasAuthority(EPermission.ADMIN_VIEW.getPermission())

                        // Používateľský panel: Prístup len pre prihlásených užívateľov (ADMIN aj USER)
                        //.requestMatchers("/dashboard").authenticated()

                        // Verejné URL: Prístupné pre kohokoľvek (aj neautentifikovaných)
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll() // static files
                        .requestMatchers("/resend_verification_code", "/verify", "/forgot-password", "/login", "/logout", "/register").permitAll()  // verification token
                        .requestMatchers("/profile/**").permitAll()
                        .requestMatchers("/news/**").permitAll()

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
                    oauth2.loginPage("/login-oauth");
                    oauth2.userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint
                                            // Pre OAuth2
                                            .userService(customOAuth2UserService)
                                            // 🔥 PRE OIDC (Google) 🔥
                                            .oidcUserService(customOIDCUserService))
                            .defaultSuccessUrl("/");
                })
                .csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
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
                case LockedException _ -> response.sendRedirect("/?modal=login&error=locked_account");
                case DisabledException _ ->
                        response.sendRedirect("/?modal=login&error=disabled_account");
                case BadCredentialsException _ ->
                        response.sendRedirect("/?modal=login&error=bad_credentials");
                case null, default -> {
                    assert authException != null;
                    response.sendRedirect("/?modal=login&error=" + authException);
                }
            }
        };
    }

}
