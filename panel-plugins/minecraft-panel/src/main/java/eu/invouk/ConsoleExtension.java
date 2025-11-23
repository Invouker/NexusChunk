package eu.invouk;

import eu.invouk.api.NavbarExtension;
import org.pf4j.Extension;

@Extension
public class ConsoleExtension implements NavbarExtension {
    @Override
    public String getTitle() {
        return "Console";
    }

    @Override
    public String getPath() {
        return "console";
    }

    @Override
    public String getPermission() {
        return "VIEW_CONSOLE";
    }
}