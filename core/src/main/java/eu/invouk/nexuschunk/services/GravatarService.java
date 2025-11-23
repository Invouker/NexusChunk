package eu.invouk.nexuschunk.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class GravatarService {

    private static final String GRAVATAR_BASE_URL = "https://www.gravatar.com/avatar/";

    /**
     * Vypočíta MD5 hash z e-mailovej adresy.
     * * @param email E-mailová adresa (už normalizovaná).
     * @return MD5 hash vo forme hexadecimálneho reťazca, alebo prázdny reťazec v prípade chyby.
     */
    private String hashEmail(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Gravatar vyžaduje, aby bol email orezaný a malý:
            String normalizedEmail = email.trim().toLowerCase();
            byte[] hash = md.digest(normalizedEmail.getBytes());

            // Konverzia na hexadecimálny reťazec
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("Chyba: MD5 algoritmus nebol nájdený.", e);
            return "";
        }
    }

    /**
     * Získa kompletnú Gravatar URL.
     *
     * @param email E-mailová adresa používateľa.
     * @param size Požadovaná veľkosť obrázka v pixeloch (napr. 100).
     * @param defaultType Predvolený typ obrázka (napr. "mp" pre mystery person, "identicon").
     * @return Finálna Gravatar URL.
     */
    public String getGravatarUrl(String email, int size, String defaultType) {
        String hash;

        // Ak je email null alebo prázdny, vrátime defaultný avatar bez hashu.
        if (email == null || email.trim().isEmpty()) {
            // Použijeme hash prázdneho reťazca (""), čo je 32-nula, ale jednoduchšie je to ignorovať
            // a priamo poslať defaultný typ. Ak je hash prázdny, Gravatar vráti default.
            hash = "";
        } else {
            hash = hashEmail(email);
        }

        // Konštrukcia URL s parametrami

        return GRAVATAR_BASE_URL + hash +
                "?s=" + size +
                "&d=" + defaultType;
    }

    /**
     * Získa Gravatar URL s predvolenými nastaveniami (veľkosť 100, typ 'identicon').
     *
     * @param email E-mailová adresa používateľa.
     * @return Finálna Gravatar URL.
     */
    public String getGravatarUrl(String email) {
        return getGravatarUrl(email, 100, "identicon");
    }
}
