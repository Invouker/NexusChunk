package eu.invouk.nexuschunk.api;

import eu.invouk.api.NavbarExtension;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class Test {

    private final PluginManager pluginManager;
    private final List<NavbarExtension> extensions; // Injektuje sa PF4J-Springom

    // Konštruktor len na injekciu (bez náročných operácií)
    public Test(PluginManager pluginManager, List<NavbarExtension> extensions) {
        this.pluginManager = pluginManager;
        this.extensions = extensions;

        pluginManager.getExtensions(NavbarExtension.class).forEach(extension -> {
            log.info("Found extension {}", extension);

            log.info("Title: {}", extension.getTitle());
            log.info("Permission: {}", extension.getPermission());
            log.info("Path: {}", extension.getPath());
        });

       /* CompletableFuture.runAsync(() ->{
            log.info("Starting up plugins");
            pluginManager.loadPlugins();
        }).whenComplete((aVoid, throwable) -> {
            pluginManager.getExtensions(NavbarExtension.class).forEach(extension -> {
                log.info("Found extension {}", extension);
            });
        });*/
        log.info(extensions.toString());
    }



}