package eu.invouk.nexuschunk.user.repositories;

import eu.invouk.nexuschunk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String userName);
}
