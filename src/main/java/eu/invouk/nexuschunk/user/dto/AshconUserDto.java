package eu.invouk.nexuschunk.user.dto;

public class AshconUserDto {

    private String uuid;
    private String username;

    // Gettery a Settery (Lombok je odporúčaný, ak ho používate)

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
