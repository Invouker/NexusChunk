package eu.invouk.nexuschunk;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Collections;

@TestConfiguration
public class TestSecurityConfiguration {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // Vytvorenie fiktívnej ClientRegistration
        ClientRegistration dummyRegistration = ClientRegistration.withRegistrationId("dummy-oauth2")
                .clientId("dummy-client-id")
                .clientSecret("dummy-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("read")
                .authorizationUri("https://dummy.com/oauth/authorize")
                .tokenUri("https://dummy.com/oauth/token")
                .userInfoUri("https://dummy.com/oauth/userinfo")
                .clientName("Dummy OAuth2 Provider")
                .build();

        // Vytvorenie repozitára s fiktívnou registráciou
        return new InMemoryClientRegistrationRepository(Collections.singletonList(dummyRegistration));
    }

}