package eu.invouk.nexuschunk.services;

import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User getUserByPrincipal(Principal principal) {
        if(principal == null) {
            log.info("Principal == null!");
            return null;
        }

        log.info("Principal: " + principal.getName());
        String email = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);

    }
}
