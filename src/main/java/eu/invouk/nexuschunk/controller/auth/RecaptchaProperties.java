package eu.invouk.nexuschunk.controller.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recaptcha") // Mapuje hodnoty z application.properties
@Data
public class RecaptchaProperties {

    private String siteKey;
    private String secretKey;
    private String verifyUrl;
}