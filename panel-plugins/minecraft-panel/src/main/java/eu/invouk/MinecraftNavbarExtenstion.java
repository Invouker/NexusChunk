package eu.invouk;

import eu.invouk.api.NavbarExtension;

public class MinecraftNavbarExtenstion implements NavbarExtension {
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
