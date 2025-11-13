package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("permissions")
public class Permission {

    public static final String ADMIN_DASHBOARD = "ADMIN_DASHBOARD";

    public static final String VIEW_NEWS = "VIEW_NEWS";
    public static final String CREATE_NEWS = "CREATE_NEWS";

    public static final String VIEW_MEMBERS = "VIEW_MEMBER";
    public static final String EDIT_MEMBER = "EDIT_MEMBER";

    public static final String VIEW_PERMISSIONS = "VIEW_PERMISSIONS";
    public static final String EDIT_PERMISSION = "EDIT_PERMISSION";

    public static final String VIEW_SETTINGS = "VIEW_SETTINGS";
    public static final String EDIT_SETTINGS = "EDIT_SETTINGS";

    public static final String VIEW_SERVER = "VIEW_SERVER";
    public static final String EDIT_SERVER = "EDIT_SERVER";

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            ADMIN_DASHBOARD,
            VIEW_NEWS,
            CREATE_NEWS,
            VIEW_MEMBERS,
            EDIT_MEMBER,
            VIEW_PERMISSIONS,
            EDIT_PERMISSION,
            VIEW_SETTINGS,
            EDIT_SETTINGS,
            VIEW_SERVER,
            EDIT_SERVER
    );

    public Permission() {
    }


}
