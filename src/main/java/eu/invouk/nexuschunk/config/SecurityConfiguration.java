package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.permissions.PERMISSION;
import eu.invouk.nexuschunk.services.CustomOAuth2UserService;
import eu.invouk.nexuschunk.services.CustomOIDCUserService;
import eu.invouk.nexuschunk.services.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
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
                        .requestMatchers("/admin/**").hasAuthority(PERMISSION.ADMIN_VIEW.getPermission())

                        // Používateľský panel: Prístup len pre prihlásených užívateľov (ADMIN aj USER)
                        .requestMatchers("/profile", "/dashboard").authenticated()

                        // Verejné URL: Prístupné pre kohokoľvek (aj neautentifikovaných)
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll() // static files
                        .requestMatchers("/resend_verification_code", "/verify", "/forgot-password", "/login", "/logout").permitAll()  // verification token

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
        return (request, response, authException) -> {
            if(authException instanceof LockedException) {
                response.sendRedirect("/?modal=login&error=locked_account");
            }else if(authException instanceof DisabledException) {
                response.sendRedirect("/?modal=login&error=disabled_account");
            } else {
                response.sendRedirect("/?modal=login&error=" + authException.toString());
            }
        };
    }

}
