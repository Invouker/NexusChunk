package eu.invouk.nexuschunk.admin.dtos;

import eu.invouk.nexuschunk.user.permissions.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private long id;

    private String userName;
    private String email;
    private String minecraftNick;
    private String uuid;
    private boolean enabled;
    private boolean locked;

    private String aboutMe;
    private String github;
    private String discord;
    private String facebook;
    private String instagram;

    private Set<String> roles;

}
