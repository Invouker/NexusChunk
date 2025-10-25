package eu.invouk.nexuschunk.auth.services;

import eu.invouk.nexuschunk.user.Role;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class CustomOIDCUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOIDCUserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("CustomOIDCUserService has been initialized");
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUserService oidcUserService = new OidcUserService();
        OidcUser oidcUser = oidcUserService.loadUser(userRequest); // 🔥 Používame OidcUserService!

        log.info("loadUser 02 - Získané atribúty OIDC používateľa");

        // Atribúty získavame z OidcUser (cez metódu getAttributes(), ktorá vráti Mapu)
        String email = oidcUser.getAttributes().get("email").toString();
        String name = oidcUser.getAttributes().get("name").toString();

        log.info("loadUser 03 - Hľadanie používateľa: {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        user = optionalUser.orElseGet(() -> registerNewUser(email, name));

        log.info("loadUser 04 - Používateľ: {} má rolu: {}", user.getEmail(), user.getRoles().iterator().next().getName());
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(user.getRoles().iterator().next().getName())
        );
        log.info("loadUser 05 {}", user);
        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(), // Použijeme ID Token získaný zo Spring OIDC servisu
                oidcUser.getUserInfo(), // Voliteľné, ale dobré na uchovanie UserInfo
                "email" // Kľúč, ktorý slúži ako 'username' (unikatne ID v rámci providera)
        );
    }

    private User registerNewUser(String email, String name) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setMinecraftNick(name);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRegistrationDate(LocalDateTime.now());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rola ROLE_USER nebola nájdená v databáze."));

        newUser.setRoles(Collections.singleton(userRole));

        log.info("Nový používateľ zaregistrovaný: {}", email);
        return userRepository.save(newUser);
    }
}

