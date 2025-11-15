package eu.invouk.nexuschunk;

public class Utils {

    public static String emptyToNull(String v) {
        return (v == null || v.isBlank()) ? null : v;
    }

}
