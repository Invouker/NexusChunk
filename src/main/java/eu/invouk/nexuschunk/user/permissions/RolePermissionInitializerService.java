package eu.invouk.nexuschunk.user.permissions;

import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servisná trieda pre inicializáciu Rolí a Povolení.
 * Metóda musí byť @Transactional, aby sa predišlo LazyInitializationException,
 * keďže sa pristupuje k role.getPermissions().
 */
@Service
public class RolePermissionInitializerService {

    private final RoleRepository roleRepository;

    public RolePermissionInitializerService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void initializeRolesAndAssignPermissions(
            Map<String, List<EPermission>> rolePermissionsMap,
            Map<String, Permission> allPermissions
    ) {

        rolePermissionsMap.forEach((roleName, requiredPermissions) -> {

            Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                Role newRole = new Role(roleName);
                return roleRepository.save(newRole);
            });

            Set<Permission> newPermissionsForRole = requiredPermissions.stream()
                    .map(pEnum -> allPermissions.get(pEnum.getPermission()))
                    .collect(Collectors.toSet());


            Set<String> currentPermissionNames = role.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            boolean needsUpdate = !currentPermissionNames.equals(
                    newPermissionsForRole.stream().map(Permission::getName).collect(Collectors.toSet())
            );

            if (needsUpdate) {
                role.setPermissions(newPermissionsForRole);
                roleRepository.save(role);
            }
        });
    }
}