package eu.invouk.nexuschunk.config;

// Importujeme novú triedu Permission, ktorá slúži ako komponent a zoznam konštánt

import eu.invouk.nexuschunk.permissions.Permission;
import eu.invouk.nexuschunk.user.permissions.RolePermissionInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Order(100)
public class RolePermissionInitializerConfig {


    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "USER", Set.of(),
            "MODERATOR", Set.of(
                    Permission.VIEW_ADMIN_DASHBOARD,
                    Permission.VIEW_NEWS,
                    Permission.VIEW_MEMBERS,
            ),
            "ADMIN", Permission.ALL_PERMISSIONS.stream().collect(Collectors.toSet())

    );

    @Bean
    public CommandLineRunner initializeRolesAndPermissions(RolePermissionInitializerService initializerService) {
        return _ -> {
            Set<String> allValidPermissions = Permission.ALL_PERMISSIONS;

            initializerService.initializeRolesAndAssignPermissions(
                    ROLE_PERMISSIONS,
                    allValidPermissions
            );
        };
    }

}