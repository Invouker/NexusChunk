package eu.invouk.nexuschunk.permissions;

import lombok.Getter;

@Getter
public enum EPermission {

    ADMIN_VIEW("ADMIN_VIEW"),
    USER_MANAGEMENT_WRITE("USER_MANAGMENT_WRITE"),
    SERVER_CONTROL("SERVER_CONTROL");

    private final String permission;
    EPermission(String permission) {
        this.permission = permission;
    }
}
