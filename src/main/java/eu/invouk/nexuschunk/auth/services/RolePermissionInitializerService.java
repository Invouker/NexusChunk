package eu.invouk.nexuschunk.auth.services;

import eu.invouk.nexuschunk.user.permissions.EPermission;
import eu.invouk.nexuschunk.user.permissions.Permission;
import eu.invouk.nexuschunk.user.permissions.Role;
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

    // Repozitár vkladáme cez konštruktor
    public RolePermissionInitializerService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional // 🔥 Táto anotácia teraz funguje, pretože volanie príde zvonku
    public void initializeRolesAndAssignPermissions(
            Map<String, List<EPermission>> rolePermissionsMap,
            Map<String, Permission> allPermissions
    ) {
        // Použijeme mapu rolí a povolení, ktorú dostaneme z konfigurácie

        rolePermissionsMap.forEach((roleName, requiredPermissions) -> {

            // 1. Nájdeme alebo vytvoríme rolu
            // Používame roleRepository, ktoré je v Service triede
            Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                Role newRole = new Role(roleName);
                return roleRepository.save(newRole);
            });

            // 2. Vytvoríme Set požadovaných povolení pre túto rolu
            Set<Permission> newPermissionsForRole = requiredPermissions.stream()
                    .map(pEnum -> allPermissions.get(pEnum.getPermission()))
                    .collect(Collectors.toSet());

            // 3. Aktuálne povolenia roly (pre jednoduché porovnanie)
            // Vďaka @Transactional už NENASTANE LazyInitializationException!
            Set<String> currentPermissionNames = role.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            // 4. Ak sa sady povolení nelíšia, nerobíme nič
            boolean needsUpdate = !currentPermissionNames.equals(
                    newPermissionsForRole.stream().map(Permission::getName).collect(Collectors.toSet())
            );

            if (needsUpdate) {
                // Ak sa líšia, prepíšeme sadu povolení a uložíme zmeny
                role.setPermissions(newPermissionsForRole);
                roleRepository.save(role);
                // Logovanie zmien by bolo vhodné
            }
        });
    }
}