package eu.invouk.nexuschunk.app.settings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table
public class AppSettings {

    @Id
    private String id;
    private String settingValue;


    public AppSettings(String id, String settingValue) {
        this.id = id;
        this.settingValue = settingValue;
    }

    public AppSettings() {

    }
}
