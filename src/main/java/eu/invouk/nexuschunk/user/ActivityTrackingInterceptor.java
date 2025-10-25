package eu.invouk.nexuschunk.user;

import eu.invouk.nexuschunk.user.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ActivityTrackingInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public ActivityTrackingInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getUserPrincipal() != null) {
            String username = request.getUserPrincipal().getName();

            Optional<User> userOptional = userRepository.findByEmail(username);

            userOptional.ifPresent(user -> {
                user.setLastActivity(LocalDateTime.now());
                userRepository.save(user);
            });
        }
        return true;
    }
}
