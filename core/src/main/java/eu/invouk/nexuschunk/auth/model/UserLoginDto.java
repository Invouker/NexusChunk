package eu.invouk.nexuschunk.auth.model;

import lombok.Data;

@Data
public class UserLoginDto {

    private String username;
    private String password;

}
