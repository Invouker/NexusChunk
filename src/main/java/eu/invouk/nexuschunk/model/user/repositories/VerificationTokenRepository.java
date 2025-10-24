package eu.invouk.nexuschunk.model.user.repositories;

import eu.invouk.nexuschunk.model.user.User;
import eu.invouk.nexuschunk.model.user.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {

    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);

}
