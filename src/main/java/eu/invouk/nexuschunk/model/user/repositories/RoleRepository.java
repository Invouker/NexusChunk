package eu.invouk.nexuschunk.model.user.repositories;

import eu.invouk.nexuschunk.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

}
