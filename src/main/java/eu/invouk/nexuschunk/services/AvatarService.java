package eu.invouk.nexuschunk.services;

import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.dto.AvatarDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AvatarService {

    private final GravatarService gravatarService;
    private final MinecraftAvatarService minecraftAvatarService;

    public AvatarService(GravatarService gravatarService, MinecraftAvatarService minecraftAvatarService) {
        this.gravatarService = gravatarService;
        this.minecraftAvatarService = minecraftAvatarService;
    }

    public AvatarDto getAvatar(User user, int size) {
        return getAvatar(user, size, "identicon");
    }

    /**
     * Získa URL avatara pre daného používateľa.
     *
     * @param user Objekt používateľa.
     * @param size Veľkosť avataru v pixeloch.
     * @param defaultGravatar Predvolený typ Gravataru (napr. "mp", "identicon").
     * @return AvatarDTO obsahujúci URL avatara, typ a zdroj.
     */
    public AvatarDto getAvatar(User user, int size, String defaultGravatar) {
        Optional<String> imageBase = minecraftAvatarService.getBase64Avatar(user.getUsername());
        if(imageBase.isPresent()) {
            return AvatarDto.createMinecraftAvatar(imageBase.get());
        } else {
            String url = gravatarService.getGravatarUrl(user.getEmail(), size, defaultGravatar);
            return AvatarDto.createGravatar(url);
        }
    }
}
