package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.model.user.Role;
import eu.invouk.nexuschunk.model.user.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class RoleInitializerConfig {

    /**
     * Zoznam všetkých povinných ROL, ktoré musia existovať v DB.
     * Používajte štandardný prefix Spring Security: ROLE_
     */
    private static final List<String> REQUIRED_ROLES = Arrays.asList(
            "ROLE_USER",
            "ROLE_MODERATOR",
            "ROLE_ADMIN"
    );

    @Bean
    public CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            log.info("Spúšťam inicializáciu povinných rol v databáze.");

            for (String roleName : REQUIRED_ROLES) {
                // Skontrolujeme, či rola už existuje
                if (roleRepository.findByName(roleName).isEmpty()) {
                    // Ak neexistuje, vytvoríme ju
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    roleRepository.save(newRole);

                    log.info("  -> Vytvorená nová rola: {}", roleName);
                }
            }
            log.info("Inicializácia rol dokončená.");
        };
    }
}