package eu.invouk.nexuschunk.auth.services;

import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(loginAttemptService.isBlocked(username)) {
            throw new LockedException("Účet je zablokovaný z príliš vela neúspešných pokusov o prihlásenie.");
        }

        User user = userRepository.findByEmail(username)
                .orElseGet(
                        () -> userRepository.findByMinecraftNick(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Uživateľ (" + username +") s podobným nicknamom sa nenašiel!")
                        ));

        user.setLastLogin(LocalDateTime.now());

        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1. Pridanie Rolí (ROLE_USER, ROLE_ADMIN, atď.)
        user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .forEach(authorities::add);

        // 2. Pridanie Povolení (ADMIN_VIEW, NEWS_CREATE, atď.)
        // Predpokladá, že getPermissions() už nikdy nevráti null (pozri predchádzajúci fix)
        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .forEach(authorities::add);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Prihlasovacie meno (email)
                user.getPassword(), // Heslo (už zašifrované!)
                user.isEnabled(),true,true,true,
                authorities
        );
    }


}
