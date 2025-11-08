package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("permissions")
public class Permission {

    public static final String ADMIN_DASHBOARD = "ADMIN_DASHBOARD";
    public static final String ADMIN_MEMBERS = "ADMIN_MEMBERS";
    public static final String CREATE_NEWS = "CREATE_NEWS";
    public static final String EDIT_NEWS = "EDIT_NEWS";

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            ADMIN_DASHBOARD,
            ADMIN_MEMBERS,
            CREATE_NEWS,
            EDIT_NEWS
    );

    public Permission() {
    }
}
