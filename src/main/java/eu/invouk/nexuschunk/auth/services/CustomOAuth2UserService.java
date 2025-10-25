package eu.invouk.nexuschunk.auth.services;

import eu.invouk.nexuschunk.user.Role;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("CustomOAuth2UserService has been initialized");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        log.info("loadUser");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        log.info("loadUser 02");
        String email = oAuth2User.getAttributes().get("email").toString();
        String name = oAuth2User.getAttributes().get("name").toString();

        log.info("loadUser 03");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        user = optionalUser.orElseGet(() -> registerNewUser(email, name));

        log.info("loadUser 04");
        Set<Role> roles = user.getRoles();
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(user.getRoles().iterator().next().getName())
        );

        // Vrátenie OAuth2User objektu, ktorý Spring Security uloží do SecurityContextu
        // Používame našu entitu (user) ako 'Principal'

        log.info("loadUser 05 {}", user);
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email" // Kľúč, ktorý slúži ako 'username' (unikatne ID v rámci providera)
        );
    }

    private User registerNewUser(String email, String name) {
        User newUser = new User();

        // Google poskytuje ID, ale pre jednoduchosť použijeme email pre unikátnu identifikáciu
        // POZOR: Pre OAuth2 používateľov nemusíme ukladať heslo, ale Spring ho vyžaduje,
        // takže použijeme fiktívny zahashovaný reťazec alebo necháme pole nullable.

        newUser.setEmail(email);
        newUser.setMinecraftNick(name);
        // POZOR: Ak má vaša entita User pole 'password' ako NOT NULL, musíte sem niečo vložiť,
        // napr. prázdny reťazec zahashovaný Bcryptom. Pre Google používateľov sa heslo nepoužíva.
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Náhodné heslo,

        // Pridelenie predvolenej roly (napr. ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rola ROLE_USER nebola nájdená v databáze."));

        newUser.setRoles(Collections.singleton(userRole));

        return userRepository.save(newUser);
    }
}
