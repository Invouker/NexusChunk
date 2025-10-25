package eu.invouk.nexuschunk.auth.model;

import lombok.Data;

@Data
public class UserRegistrationDto {

    private String email;
    private String password;
    private String confirmPassword;
    private String minecraftNick;

}
