package eu.invouk.nexuschunk.config;

// Importujeme novú triedu Permission, ktorá slúži ako komponent a zoznam konštánt
import eu.invouk.nexuschunk.permissions.Permission;
// Musíme aktualizovať import servisu, ak ste ho premenovali alebo presunuli
import eu.invouk.nexuschunk.user.permissions.RolePermissionInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Order(100)
public class RolePermissionInitializerConfig {

    // 🔥 NOVÁ MAPA: Teraz mapujeme String (role name) na Set<String> (permission names)
    // Používame konštanty z nového komponentu Permission
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "USER", Set.of(),
            "MODERATOR", Set.of(
                    Permission.CREATE_NEWS // Pridajte sem reálne oprávnenia, ktoré má MODERATOR mať
            ),
            "ADMIN", Permission.ALL_PERMISSIONS.stream().collect(Collectors.toSet())
            /* Príklad, ak by ste chceli definovať konkrétny Set pre ADMINa:
            "ADMIN", Set.of(
                    Permission.CREATE_NEWS,
                    Permission.EDIT_NEWS
                    // ... ďalšie nové oprávnenia ...
            )
            */
    );

    @Bean
    public CommandLineRunner initializeRolesAndPermissions(
            // PermissionRepository už nepotrebujeme na priamu inicializáciu Povolení,
            // pretože to robí inicializačný servis interne. Preto ju odstraňujeme.
            RolePermissionInitializerService initializerService
    ) {
        return _ -> {

            // 1. Zoznam všetkých platných oprávnení
            Set<String> allValidPermissions = Permission.ALL_PERMISSIONS;

            // 2. Volanie transakčnej metódy v službe
            // Servis teraz dostane Mapu Rolí a Set všetkých platných oprávnení
            initializerService.initializeRolesAndAssignPermissions(
                    ROLE_PERMISSIONS,
                    allValidPermissions
            );
        };
    }

    // 🔥 Pôvodná metóda initializePermissions už nie je potrebná a bola odstránená
}