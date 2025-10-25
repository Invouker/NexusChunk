package eu.invouk.nexuschunk.user.repositories;

import eu.invouk.nexuschunk.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMinecraftUuid(String email);
    Optional<User> findByMinecraftNick(String minecraftNick);
}
