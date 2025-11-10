package eu.invouk.nexuschunk.services;

import eu.invouk.nexuschunk.user.dto.AshconUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Slf4j
@Service
public class MinecraftApiService {

    private final WebClient webClient;
    private static final String ASHCON_API_URL = "https://api.ashcon.app/mojang/v2/user/";

    public MinecraftApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(ASHCON_API_URL).build();
    }

    /**
     * Získa UUID hráča na základe jeho Minecraft prezývky.
     * UUID je vrátené vo formáte s pomlčkami (napr. a0a1a2a3-...).
     *
     * @param username Minecraft prezývka.
     * @return Optional obsahujúci UUID, ak bol nájdený, inak prázdny Optional.
     */
    @Cacheable(value = "minecraftAvatars", key = "#username.toLowerCase()", condition = "#username != null")
    public Optional<String> getUuidByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        username = username.replace(" ", "");

        try {
            log.info("Hľadám UUID pre prezývku: {}", username);

            // Vykonanie neblokujúcej HTTP požiadavky
            String finalUsername = username;
            AshconUserDto userDto = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("{username}").build(finalUsername))
                    .retrieve()
                    .bodyToMono(AshconUserDto.class)
                    .block(); // Blokuje, aby bolo možné vrátiť synchrónny výsledok

            if (userDto != null && userDto.getUuid() != null) {
                log.info("Úspešne získané UUID pre {}: {}", username, userDto.getUuid());
                return Optional.of(userDto.getUuid());
            }

        } catch (WebClientResponseException.NotFound | WebClientResponseException.BadRequest ex) {
            // Ak Ashcon vráti 404 (používateľ nebol nájdený)
            log.warn("Hráč s prezývkou '{}' nebol nájdený.", username); }
        catch (Exception ex) {
            // Ak nastane iná chyba (napr. problém so sieťou, timeout)
            log.error("Chyba pri získavaní UUID pre prezývku '{}'.", username, ex);
        }

        return Optional.empty();
    }

}
