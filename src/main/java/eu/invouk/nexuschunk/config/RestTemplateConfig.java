package eu.invouk.nexuschunk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Definuje a vracia inštanciu RestTemplate.
     * Týmto sa RestTemplate zaregistruje ako Bean a stane sa dostupným
     * pre automatické vstreknutie (@Autowired) v iných službách.
     */
    @Bean
    public RestTemplate restTemplate() {
        // Môžete tu pridať custom konfiguráciu, napr. timeouty alebo interceptory,
        // ale pre základné použitie stačí toto.
        return new RestTemplate();
    }
}