package eu.invouk.nexuschunk.services;

import eu.invouk.nexuschunk.model.user.User;
import eu.invouk.nexuschunk.model.user.repositories.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
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

        loginAttemptService.loginSuccess(username);

        /*if(!user.isEnabled())
            throw new LockedException("User is disabled.");
*/
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Prihlasovacie meno (email)
                user.getPassword(), // Heslo (už zašifrované!)
                user.isEnabled(),true,true,true,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}
