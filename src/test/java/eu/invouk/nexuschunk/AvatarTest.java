package eu.invouk.nexuschunk;

import eu.invouk.nexuschunk.services.AvatarService;
import eu.invouk.nexuschunk.services.GravatarService;
import eu.invouk.nexuschunk.services.MinecraftApiService;
import eu.invouk.nexuschunk.services.MinecraftAvatarService;
import eu.invouk.nexuschunk.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.Optional;

@SpringBootTest
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
        // --- Testy API volaní ---

        Optional<String> uuid = minecraftApiService.getUuidByUsername("Invouk");
        assert uuid.isPresent();

        String avatar = gravatarService.getGravatarUrl("12XpresS12@gmail.com");
        assert avatar != null;
        System.out.println(avatar);

        Optional<byte[]> image = minecraftAvatarService.getAvatarImageBytes("Invouk");
        assert image.isPresent();

        String imageBase = Base64.getEncoder().encodeToString(image.get());
        assert imageBase != null;
        System.out.println(imageBase);


        User targetUser = new User();
        targetUser.setMinecraftNick("Invouk");
        targetUser.setEmail("test@example.com");


        Object result = avatarService.getAvatar(targetUser, 100);

        assert result != null;
        System.out.println("Výsledok získania avatara pre targetUser: " + result);
    }
}
