package eu.invouk;

import eu.invouk.api.NavbarExtension;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class MinecraftPlugin extends Plugin {

    public MinecraftPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Minecraft panel plugin loaded ." + this.getWrapper().getDescriptor().getVersion());
    }

    // Odporúčanie: Pridaj aj stop()
    @Override
    public void stop() {
        log.info("Panel plugin stopped");
    }

}
