package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("permissions")
public class Permission {

    public static final String VIEW_ADMIN_DASHBOARD = "VIEW_ADMIN_DASHBOARD";

    public static final String VIEW_NEWS = "VIEW_NEWS";
    public static final String CREATE_NEWS = "CREATE_NEWS";

    public static final String VIEW_MEMBERS = "VIEW_MEMBER";
    public static final String EDIT_MEMBER = "EDIT_MEMBER";

    public static final String VIEW_PERMISSIONS = "VIEW_PERMISSIONS";
    public static final String EDIT_PERMISSIONS = "EDIT_PERMISSIONS";
    public static final String CREATE_PERMISSIONS_ROLE = "CREATE_PERMISSIONS_ROLE";

    public static final String VIEW_SETTINGS = "VIEW_SETTINGS";
    public static final String EDIT_SETTINGS = "EDIT_SETTINGS";

    public static final String VIEW_SERVER = "VIEW_SERVER";
    public static final String EDIT_SERVER = "EDIT_SERVER";
    public static final String CONTROL_SERVER = "CONTROL_SERVER";

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            VIEW_ADMIN_DASHBOARD,
            VIEW_NEWS,
            CREATE_NEWS,
            VIEW_MEMBERS,
            EDIT_MEMBER,
            VIEW_PERMISSIONS,
            EDIT_PERMISSIONS,
            CREATE_PERMISSIONS_ROLE,
            VIEW_SETTINGS,
            EDIT_SETTINGS,
            VIEW_SERVER,
            EDIT_SERVER,
            CONTROL_SERVER
    );

    public Permission() {
    }


}
