package eu.invouk.nexuschunk.permissions;

import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.Set;

@Component("permissions")
public class Permission {

    public static final Map<String, String> PERMISSION_DESCRIPTIONS;

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
    public static final String RELOAD_SETTINGS_ACTUATOR = "RELOAD_SETTINGS_ACTUATOR";

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
            RELOAD_SETTINGS_ACTUATOR,
            VIEW_SERVER,
            EDIT_SERVER,
            CONTROL_SERVER
    );

    static {

        // --- ZADEFINOVANIE POPISOV TU ---
        // ---------------------------------

        PERMISSION_DESCRIPTIONS = Map.ofEntries(
                Map.entry(VIEW_ADMIN_DASHBOARD, "Zobrazenie administrátorskej nástenky"),
                Map.entry(VIEW_NEWS, "Prezeranie noviniek"),
                Map.entry(CREATE_NEWS, "Vytváranie nových noviniek"),
                Map.entry(VIEW_MEMBERS, "Prezeranie zoznamu členov"),
                Map.entry(EDIT_MEMBER, "Úprava profilu člena"),
                Map.entry(VIEW_PERMISSIONS, "Prezeranie rolí a oprávnení"),
                Map.entry(EDIT_PERMISSIONS, "Úprava pridelených oprávnení iným rolám"),
                Map.entry(CREATE_PERMISSIONS_ROLE, "Vytváranie nových rolí"),
                Map.entry(VIEW_SETTINGS, "Prezeranie systémových nastavení"),
                Map.entry(EDIT_SETTINGS, "Úprava systémových nastavení"),
                Map.entry(RELOAD_SETTINGS_ACTUATOR, "Obnovenie systémových nastavení"),
                Map.entry(VIEW_SERVER, "Prezeranie informácií o stave servera"),
                Map.entry(EDIT_SERVER, "Zmena nastavení servera"),
                Map.entry(CONTROL_SERVER, "Kontrola stavu a reštart servera"));
    }



    public Permission() {
    }


}
