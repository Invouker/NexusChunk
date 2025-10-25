package eu.invouk.nexuschunk.user.dto;

import lombok.Data;

@Data
public class AvatarDto {

    public enum EAvatarType {
        MINECRAFT, GRAVATAR
    }

    private String data;
    private final EAvatarType source; // Napr. "Crafatar", "Gravatar", "Default"

    public AvatarDto(String data, EAvatarType source) {
        this.data = data;
        this.source = source;
    }



    /**
     * Factory metóda pre avatar z Minecraft API (Crafatar).
     */
    public static AvatarDto createMinecraftAvatar(String base64Image) {
        String prefixedData = base64Image.startsWith("data:") ?
                base64Image :
                "data:image/png;base64," + base64Image;
        return new AvatarDto(prefixedData,EAvatarType.MINECRAFT);
    }

    /**
     * Factory metóda pre avatar z Gravatar API.
     */
    public static AvatarDto createGravatar(String url) {
        return new AvatarDto(url, EAvatarType.GRAVATAR);
    }

}
