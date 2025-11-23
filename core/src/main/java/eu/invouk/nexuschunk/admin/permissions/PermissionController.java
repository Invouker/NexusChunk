package eu.invouk.nexuschunk.admin.permissions;

import eu.invouk.nexuschunk.admin.dtos.RoleCreationDto;
import eu.invouk.nexuschunk.admin.dtos.RolePermissionsDto;
import eu.invouk.nexuschunk.user.permissions.Permission;
import eu.invouk.nexuschunk.user.permissions.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/permissions")
@PreAuthorize("hasAuthority(@permissions.VIEW_PERMISSIONS)")
@Slf4j
public class PermissionController {

    private final RoleManagementService roleManagementService;

    public PermissionController(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }


    /**
     * Zobrazí hlavnú stránku pre správu rolí a oprávnení.
     * @param selectedRoleId ID aktuálne vybranej roly
     */
    @GetMapping
    public String showRoleManagement(Model model, @RequestParam("roleId") Optional<Long> selectedRoleId) {

        List<Role> allRoles = roleManagementService.findAllRoles();
        model.addAttribute("roles", allRoles);

        model.addAttribute("newRole", new RoleCreationDto("", ""));

        Role selectedRole = null;
        if (selectedRoleId.isPresent()) {
            try {
                selectedRole = roleManagementService.findRoleById(selectedRoleId.get());
            } catch (Exception e) {
                log.error("Role not found", e);
            }
        }

        if (selectedRole == null && !allRoles.isEmpty()) {
            selectedRole = allRoles.getFirst();
        }

        if (selectedRole != null) {
            model.addAttribute("selectedRole", selectedRole);
            List<Permission> allPermissions = roleManagementService.findAllPermissions();

            model.addAttribute("groupedPermissions", getMappedPermissions(allPermissions));
            Set<String> assignedPermissions = selectedRole.getPermissions().stream()
                    .map(Permission::getName) // Správne volanie metódy getName() na Permission entite
                    .collect(Collectors.toSet());
            model.addAttribute("assignedPermissions", assignedPermissions);
        }

        return "admin/permission";
    }

    private Map<String, List<Permission>> getMappedPermissions(List<Permission> allPermissions) {
        return allPermissions.stream()
                .collect(Collectors.groupingBy(permission -> {
                    String name = permission.getName();
                    int firstUnderscore = name.indexOf('_');

                    if (firstUnderscore < 0) {
                        return "OTHER";
                    }

                    int lastUnderscore = name.lastIndexOf('_');

                    String mainPart;

                    if (firstUnderscore == lastUnderscore) {
                        mainPart = name.substring(firstUnderscore + 1);
                    } else {
                        mainPart = name.substring(firstUnderscore + 1, lastUnderscore);
                    }
                    return mainPart.toUpperCase();
                }));
    }


    /**
     * Spracuje vytvorenie novej roly.
     */
    @PostMapping("/create")
    public String createRole(RoleCreationDto dto, RedirectAttributes ra) {
        try {
            Role newRole = roleManagementService.createRole(dto);
            ra.addFlashAttribute("successMessage", "Rola '" + newRole.getName() + "' bola úspešne vytvorená.");

            return "redirect:/admin/permissions?roleId=" + newRole.getId();
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            ra.addFlashAttribute("newRole", dto);

            return "redirect:/admin/permissions";
        }
    }

    /**
     * Spracuje uloženie upravených oprávnení pre rolu.
     */
    @PostMapping("/update")
    public String updateRolePermissions(
            @RequestParam("roleId") Long roleId,
            @RequestParam(value = "permissionCodes", required = false) Set<String> permissionCodes,
            RedirectAttributes ra) {

        // Ak nie je zaškrtnuté žiadne povolenie, Set bude null, nastavíme ho na prázdny Set
        if (permissionCodes == null) {
            permissionCodes = Set.of();
        }

        RolePermissionsDto dto = new RolePermissionsDto(roleId, permissionCodes);
        roleManagementService.updateRolePermissions(dto);

        ra.addFlashAttribute("successMessage", "Oprávnenia pre rolu ID " + roleId + " boli úspešne aktualizované.");
        return "redirect:/admin/permissions?roleId=" + roleId;
    }

    /**
     * Spracuje zmazanie roly.
     */
    @PostMapping("/delete/{roleId}")
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes ra) {
        try {
            roleManagementService.deleteRole(roleId);
            ra.addFlashAttribute("successMessage", "Rola ID " + roleId + " bola úspešne zmazaná.");
            return "redirect:/admin/permissions";
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("errorMessage", "Rola nebola nájdená.");
            return "redirect:/admin/permissions";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Rolu nie je možné zmazať. Uistite sa, že ju nepoužíva žiadny používateľ.");
            return "redirect:/admin/permissions?roleId=" + roleId;
        }
    }

}
