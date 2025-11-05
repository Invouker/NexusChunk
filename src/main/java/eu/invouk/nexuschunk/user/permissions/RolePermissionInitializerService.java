package eu.invouk.nexuschunk.user.permissions;

import eu.invouk.nexuschunk.user.permissions.repositories.PermissionRepository;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PermissionRepository permissionRepository; // Potrebujeme pre prácu s Entitou Permission

    public RolePermissionInitializerService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Pôvodný kód sa spoliehal na dynamické EPermission a mapovanie,
     * toto je upravené na prácu s Stringami a Repository pre Entity Permission.
     * * @param rolePermissionsMap Mapa RoleName (String) na zoznam PermissionName (String)
     * @param allValidPermissions Set platných názvov oprávnení, napr. Permission.ALL_PERMISSIONS
     */
    @Transactional
    public void initializeRolesAndAssignPermissions(
            Map<String, Set<String>> rolePermissionsMap,
            Set<String> allValidPermissions
    ) {
        // 1. Zabezpečíme, že všetky platné String oprávnenia existujú v DB ako Entity Permission
        // Toto je kľúčové, ak stále používate JPA Entitu Permission
        Map<String, eu.invouk.nexuschunk.user.permissions.Permission> existingPermissions = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        eu.invouk.nexuschunk.user.permissions.Permission::getName,
                        p -> p
                ));

        allValidPermissions.forEach(permName -> {
            if (!existingPermissions.containsKey(permName)) {
                // Vytvoríme a uložíme novú Entitu Permission, ak neexistuje
                eu.invouk.nexuschunk.user.permissions.Permission newPerm =
                        new eu.invouk.nexuschunk.user.permissions.Permission(permName);
                permissionRepository.save(newPerm);
                existingPermissions.put(permName, newPerm); // Pridáme do mapy
            }
        });

        rolePermissionsMap.forEach((roleName, requiredPermissionNames) -> {

            // Načítame existujúcu rolu alebo vytvoríme novú
            eu.invouk.nexuschunk.user.permissions.Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                eu.invouk.nexuschunk.user.permissions.Role newRole = new eu.invouk.nexuschunk.user.permissions.Role(roleName);
                return roleRepository.save(newRole);
            });

            // Zostavíme set objektov Permission (Entít) pre danú rolu
            Set<eu.invouk.nexuschunk.user.permissions.Permission> newPermissionsForRole = requiredPermissionNames.stream()
                    .map(existingPermissions::get) // Získame Entitu Permission podľa String názvu
                    .collect(Collectors.toSet());

            Set<String> currentPermissionNames = role.getPermissions().stream()
                    .map(eu.invouk.nexuschunk.user.permissions.Permission::getName)
                    .collect(Collectors.toSet());

            boolean needsUpdate = !currentPermissionNames.equals(requiredPermissionNames); // Používame priamo existujúcu premennú

            if (needsUpdate) {
                // Ak sa sady oprávnení nezhodujú, aktualizujeme ich
                role.setPermissions(newPermissionsForRole);
                roleRepository.save(role);
            }
        });
    }
}