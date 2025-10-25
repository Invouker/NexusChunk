package eu.invouk.nexuschunk.config;

import eu.invouk.nexuschunk.user.ActivityTrackingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ActivityTrackingInterceptor activityTrackingInterceptor;

    public WebConfig(ActivityTrackingInterceptor activityTrackingInterceptor) {
        this.activityTrackingInterceptor = activityTrackingInterceptor;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registrácia interceptora. Aplikuje sa na všetky cesty (/**)
        registry.addInterceptor(activityTrackingInterceptor);
    }

}
