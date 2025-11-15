package eu.invouk.nexuschunk.admin.permissions;

import eu.invouk.nexuschunk.admin.dtos.RoleCreationDto;
import eu.invouk.nexuschunk.admin.dtos.RolePermissionsDto;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.permissions.Permission;
import eu.invouk.nexuschunk.user.permissions.Role;
import eu.invouk.nexuschunk.user.permissions.repositories.PermissionRepository;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleManagementService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public RoleManagementService(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rola s ID " + id + " nebola nájdená."));
    }

    public List<Permission> findAllPermissions() {
        return permissionRepository.findAllByOrderByNameAsc();
    }

    public Role createRole(RoleCreationDto dto) {
        if (roleRepository.findByName(dto.newRoleName()).isPresent()) {
            throw new IllegalArgumentException("Rola s názvom " + dto.newRoleName() + " už existuje.");
        }
        Role newRole = new Role(dto.newRoleName());
        return roleRepository.save(newRole);
    }

    @Transactional
    public void updateRolePermissions(RolePermissionsDto dto) {
        Role role = findRoleById(dto.roleId());

        // Načítanie existujúcich povolení podľa kódov
        Set<Permission> newPermissions = dto.permissionCodes().stream()
                .map(permissionRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        role.setPermissions(newPermissions);
        roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role roleToDelete = findRoleById(roleId);

        // 1. KONTROLA: Zabránime zmazaniu kritickej roly (napr. USER/ADMIN)
        if (roleToDelete.getName().equals("USER") || roleToDelete.getName().equals("ADMIN")) {
            throw new IllegalStateException("Kritické roly (USER alebo ADMIN) nie je možné zmazať.");
        }

        // 2. VYČISTENIE: Nájdeme všetkých používateľov, ktorí majú túto rolu.
        List<User> usersWithRole = userRepository.findAllByRolesContains(roleToDelete);

        // 3. ODSTRÁNENIE: Odstránime rolu zo sady rolí každého používateľa.
        for (User user : usersWithRole) {
            user.getRoles().remove(roleToDelete);
        }

        // Uloženie zmien (Cascade.MERGE automaticky ukladá zmeny v rámci transakcie, ale je bezpečnejšie zavolať save/flush)
        userRepository.saveAll(usersWithRole);

        // 4. ZMAZANIE: Teraz je bezpečné zmazať rolu, pretože už neexistujú externé referencie
        roleRepository.delete(roleToDelete);
    }
}