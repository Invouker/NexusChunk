package eu.invouk.nexuschunk.permissions;

import lombok.Getter;

@Getter
public enum PERMISSION {

    ADMIN_VIEW("ADMIN_VIEW")
    ;
    private final String permission;
    PERMISSION(String permission) {
        this.permission = permission;
    }
}
