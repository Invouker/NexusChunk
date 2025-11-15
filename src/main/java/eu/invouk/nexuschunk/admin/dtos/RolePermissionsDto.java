package eu.invouk.nexuschunk.admin.dtos;

import java.util.Set;

public record RolePermissionsDto(
        Long roleId,
        Set<String> permissionCodes
) {}