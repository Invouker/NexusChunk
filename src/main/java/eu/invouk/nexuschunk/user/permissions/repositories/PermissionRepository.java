package eu.invouk.nexuschunk.user.permissions.repositories;

import eu.invouk.nexuschunk.user.permissions.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Optional<Permission> findByName(String name);
    List<Permission> findAllByOrderByNameAsc();
}
