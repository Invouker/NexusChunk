package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("permissions")
public class Permission {

    public static final String CREATE_NEWS = "CREATE_NEWS";
    public static final String EDIT_NEWS = "EDIT_NEWS";

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            CREATE_NEWS,
            EDIT_NEWS
    );

    public Permission() {
    }
}
