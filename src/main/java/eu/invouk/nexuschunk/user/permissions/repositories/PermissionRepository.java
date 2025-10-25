package eu.invouk.nexuschunk.user.permissions.repositories;

import eu.invouk.nexuschunk.user.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Optional<Permission> findByName(String name);
}
