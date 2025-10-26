package eu.invouk.nexuschunk.auth.model;

import lombok.Data;

@Data
public class UserRegistrationDto {

    private String email;
    private String username;
    private String password;
    private String confirmPassword;

}
