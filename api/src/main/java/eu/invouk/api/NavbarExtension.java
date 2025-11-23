package eu.invouk.api;

import org.pf4j.ExtensionPoint;


public interface NavbarExtension extends ExtensionPoint {

    String getTitle();
    String getPath();
    String getPermission();

}
