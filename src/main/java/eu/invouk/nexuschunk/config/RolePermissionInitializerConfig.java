package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.auth.services.RolePermissionInitializerService;
import eu.invouk.nexuschunk.user.permissions.EPermission;
import eu.invouk.nexuschunk.user.permissions.Permission;
import eu.invouk.nexuschunk.user.permissions.repositories.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.*;

@Configuration
@Order(100)
public class RolePermissionInitializerConfig {

    // Žiadne špeciálne permisie pre bežného užívateľa
    // Mapa zostáva
    private static final Map<String, List<EPermission>> ROLE_PERMISSIONS = Map.of(
            "ROLE_USER", List.of(),
            "ROLE_MODERATOR", List.of(
                    EPermission.ADMIN_VIEW
            ),
            "ROLE_ADMIN", List.of(
                    EPermission.ADMIN_VIEW,
                    EPermission.USER_MANAGEMENT_WRITE,
                    EPermission.SERVER_CONTROL
            )
    );

    @Bean
    public CommandLineRunner initializeRolesAndPermissions(
            // Vložíme len tie závislosti, ktoré potrebujeme v tejto triede
            PermissionRepository permissionRepository,
            // 🔥 Vložíme novú transakčnú službu
            RolePermissionInitializerService initializerService
    ) {
        return args -> {
            // 1. Inicializácia Povolení (táto časť nepotrebuje transakciu)
            Map<String, Permission> createdPermissions = initializePermissions(permissionRepository);

            // 2. Volanie transakčnej metódy v službe (Service)
            initializerService.initializeRolesAndAssignPermissions(
                    ROLE_PERMISSIONS,
                    createdPermissions
            );
        };
    }

    // Metóda pre inicializáciu Povolení (zostáva bežná, netransakčná)
    private Map<String, Permission> initializePermissions(PermissionRepository permissionRepository) {
        Map<String, Permission> permissionsMap = new HashMap<>();

        for (EPermission ePermission : EPermission.values()) {
            Optional<Permission> existingPermission = permissionRepository.findByName(ePermission.getPermission());

            Permission permission = existingPermission.orElseGet(() -> {
                Permission newPermission = new Permission(ePermission.getPermission());
                permissionRepository.save(newPermission);
                return newPermission;
            });
            permissionsMap.put(ePermission.getPermission(), permission);
        }
        return permissionsMap;
    }

    // 🔥 Pôvodná metóda initializeRolesAndAssignPermissions je presunutá do Service!
}