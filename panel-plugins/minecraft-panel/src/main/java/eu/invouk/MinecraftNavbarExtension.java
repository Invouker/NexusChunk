package eu.invouk;

import eu.invouk.api.NavbarExtension;
import org.pf4j.Extension;

@Extension
public class MinecraftNavbarExtension implements NavbarExtension {
    @Override
    public String getTitle() {
        return "Minecraft";
    }

    @Override
    public String getPath() {
        return "/minecraft";
    }

    @Override
    public String getPermission() {
        return "minecraft_view";
    }
}
