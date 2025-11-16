package eu.invouk.nexuschunk.app;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
@Slf4j
public class AppSettingsService {

    private String appName;

    private boolean isMaintenanceMode;
    private boolean isRegistrationEnabled;
    private boolean isLoginEnabled;

    private int loginLimitAttempts;
    private int lockAccountAfterMinutes;

    private int minimumPasswordLength;

    protected final AppRepository appRepository;

    public AppSettingsService(AppRepository appRepository) {
        this.appRepository = appRepository;


        appName = loadAppSettingOrDefault("app.name", "Nexus Chunk", String.class);
        isMaintenanceMode = loadAppSettingOrDefault("app.maintenance.mode", false, Boolean.class);
        isRegistrationEnabled = loadAppSettingOrDefault("app.registration.enabled", true, Boolean.class);
        isLoginEnabled = loadAppSettingOrDefault("app.login.enabled", true, Boolean.class);
        loginLimitAttempts = loadAppSettingOrDefault("app.login.limit.attempts", 5, Integer.class);
        lockAccountAfterMinutes = loadAppSettingOrDefault("app.lock.time.account", 15, Integer.class);
        minimumPasswordLength = loadAppSettingOrDefault("app.login.minimum.password.length", 6, Integer.class);

        log.info("Loaded app settings.");
    }

    @SuppressWarnings("unchecked")
    public <T> T loadAppSettingOrDefault(String key, T defaultValue, Class<T> targetType) {

        Optional<AppSettings> setting = appRepository.findById(key);

        if (setting.isPresent()) {
            String valueString = setting.get().getSettingValue();
            try {
                return (T) convertValueToTargetType(valueString, targetType);
            } catch (Exception e) {
                System.err.println("Chyba konverzie pre kľúč: " + key + ". Používa sa predvolená hodnota.");
                return defaultValue;
            }
        } else
            appRepository.save(new AppSettings(key, defaultValue.toString()));
        return defaultValue;
    }

    /**
     * Pomocná metóda pre konverziu String hodnoty na cieľový typ.
     */
    private Object convertValueToTargetType(String value, Class<?> targetType) {
        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            // Bezpečnejšia konverzia pre booleany
            return Boolean.parseBoolean(value.toLowerCase());
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.parseLong(value);
        }
        // Pridajte ďalšie typy podľa potreby (Double, Float, atď.)

        throw new IllegalArgumentException("Nepodporovaný cieľový typ konverzie: " + targetType.getName());
    }
}
