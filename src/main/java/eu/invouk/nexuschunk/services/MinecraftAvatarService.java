package eu.invouk.nexuschunk.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Optional;


@Slf4j
@Service
public class MinecraftAvatarService {

    private final MinecraftApiService minecraftApiService;
    private final WebClient webClient;
    private static final String CRAFATAR_BASE_URL = "https://crafatar.com";
    private static final int AVATAR_SIZE = 100; // Statická veľkosť pre avatar

    // Injektuje existujúcu službu a WebClient.Builder
    public MinecraftAvatarService(MinecraftApiService minecraftApiService, WebClient.Builder webClientBuilder) {
        this.minecraftApiService = minecraftApiService;
        // Vytvorenie dedikovaného WebClienta pre Crafatar, ak by bolo treba
        this.webClient = webClientBuilder.baseUrl(CRAFATAR_BASE_URL).build();
    }

    /**
     * Získa avatar obrázok hráča, ako base64 obrázok.
     *
     * @param username Minecraft prezývka.
     * @return Optional obsahujúci base64 encoded obrázok v png.
     */
    @Cacheable(value = "minecraftAvatars", key = "#username.toLowerCase()")
    public Optional<String> getBase64Avatar(String username) {
        Optional<byte[]> bytesAvatar = getAvatarImageBytes(username);

        if(bytesAvatar.isPresent()) {
            byte[] imageByte = bytesAvatar.get();
            String imageBase = Base64.getEncoder().encodeToString(imageByte);
            return Optional.of(imageBase);
        }
        return Optional.empty();
    }

    /**
     * Získa avatar obrázok hráča, ako pole bajtov na základe jeho prezývky.
     *
     * @param username Minecraft prezývka.
     * @return Optional obsahujúci pole bajtov obrázka (PNG), ak bol nájdený, inak prázdny Optional.
     */
    @Cacheable(value = "minecraftAvatars", key = "#username.toLowerCase()")
    public Optional<byte[]> getAvatarImageBytes(String username) {
        // 1. Získanie UUID pomocou existujúcej služby
        Optional<String> uuidOptional = minecraftApiService.getUuidByUsername(username);

        if (uuidOptional.isEmpty()) {
            log.info("Nemožno získať avatar: UUID pre používateľa '{}' nebol nájdený.", username);
            return Optional.empty();
        }

        String uuid = uuidOptional.get();

        // 2. Stiahnutie obrázka z Crafatar
        try {
            log.info("Sťahujem avatar pre UUID: {}", uuid);

            // Crafatar URL: /avatars/{uuid}?size=100
            byte[] imageBytes = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/avatars/{uuid}")
                            .queryParam("size", AVATAR_SIZE)
                            .build(uuid))
                    .retrieve()
                    .bodyToMono(byte[].class) // Očakáva binárny obsah (pole bajtov)
                    .block(); // Blokuje pre synchrónne vrátenie výsledku

            assert imageBytes != null;
            log.info("Úspešne stiahnutý avatar pre {}. ({} bajtov)", username, imageBytes.length);
            return Optional.of(imageBytes);

        } catch (WebClientResponseException ex) {
            // Ak Crafatar vráti 4xx alebo 5xx, avšak Crafatar pre neznáme UUID zvyčajne vráti default obrázok,
            // takže toto by nemalo nastať pri platnom UUID.
            log.warn("Chyba alebo chýbajúci avatar z Crafatar pre UUID: {}", uuid, ex);
            return Optional.empty();
        } catch (Exception ex) {
            // Iná neočakávaná chyba (napr. sieťová, problém s konverziou)
            log.error("Neočakávaná chyba pri sťahovaní avataru pre UUID '{}'.", uuid, ex);
            return Optional.empty();
        }
    }
}
