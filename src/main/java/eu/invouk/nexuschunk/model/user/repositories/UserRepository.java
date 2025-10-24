package eu.invouk.nexuschunk.model.user.repositories;

import eu.invouk.nexuschunk.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMinecraftUuid(String email);
    Optional<User> findByMinecraftNick(String minecraftNick);
}
