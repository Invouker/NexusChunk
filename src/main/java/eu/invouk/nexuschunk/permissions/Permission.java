package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("permissions")
public class Permission {

    public static final String ADMIN_DASHBOARD = "ADMIN_DASHBOARD";

    //NEWS
    public static final String CREATE_NEWS = "CREATE_NEWS";
    public static final String EDIT_NEWS = "EDIT_NEWS";

    //MEMBER PERMISSIONS
    public static final String EDIT_MEMBER = "EDIT_MEMBER";

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            ADMIN_DASHBOARD,
            CREATE_NEWS,
            EDIT_NEWS,
            EDIT_MEMBER
    );

    public Permission() {
    }
}
