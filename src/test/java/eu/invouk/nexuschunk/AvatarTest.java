package eu.invouk.nexuschunk;

import eu.invouk.nexuschunk.services.AvatarService;
import eu.invouk.nexuschunk.services.GravatarService;
import eu.invouk.nexuschunk.services.MinecraftApiService;
import eu.invouk.nexuschunk.services.MinecraftAvatarService;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.dto.AvatarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Base64;
import java.util.Optional;

@SpringBootTest
@Import(TestSecurityConfiguration.class)
class AvatarTest {

    @Autowired
    private MinecraftApiService minecraftApiService;

    @Autowired
    private MinecraftAvatarService minecraftAvatarService;

    @Autowired
    private GravatarService gravatarService;

    @Autowired
    private AvatarService avatarService;

    @Test
    void contextLoads() {
        User targetUser = new User();
        targetUser.setMinecraftNick("Invouk");
        targetUser.setEmail("test@example.com");

        Object result = avatarService.getAvatar(targetUser, 100);
        assert result != null;
        String avatarInfo;

        if (result instanceof byte[]) {
            byte[] imageBytes = (byte[]) result;
            avatarInfo = "Image Bytes (Base64): " + Base64.getEncoder().encodeToString(imageBytes).substring(0, 50) + "...";
            assert imageBytes.length > 0;

        } else if (result instanceof String) {
            avatarInfo = "Avatar URL: " + result;
            assert ((String) result).startsWith("http");

        } else if (result instanceof AvatarDto) {
            AvatarDto dto = (AvatarDto) result;
            avatarInfo = "Avatar DTO returned. Type: " + dto.getData() + ", URL: " + dto.getSource();
            assert dto.getData() != null;

        } else { // Pôvodná else vetva
            avatarInfo = "Unknown Result Type: " + result.getClass().getName();
            // Túto aserciu (ktorá zlyhala) teraz môžeme odstrániť alebo nechať
            // pre budúce nečakané typy. Pre test stačí povoliť DTO.
            assert false : "AvatarService returned truly unexpected type: " + result.getClass().getName();
        }

        System.out.println("Výsledok získania avatara pre targetUser: " + avatarInfo);

    }
}
