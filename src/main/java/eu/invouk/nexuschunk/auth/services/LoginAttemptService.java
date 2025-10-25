package eu.invouk.nexuschunk.auth.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private final int MAX_ATTEMPTS = 2; // DEBUG ONLY
    private final LoadingCache<String, AtomicInteger> attemptsCache;

    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public AtomicInteger load(String key) {
                        return new AtomicInteger(0);
                    }
                });
    }

    private String getNormalizedKey(String key) {
        if (key == null) return "";
        return key.toLowerCase();
    }

    public void loginFailed(String key) {
        String normalizedKey = getNormalizedKey(key);

        try {

            int currentAttempts = attemptsCache.get(normalizedKey).incrementAndGet();
            log.info("Neúspešný pokus pre: {} (Aktuálny pošet: {})", normalizedKey, currentAttempts);

        } catch (ExecutionException e) {
            log.error("Chyba pri prístupe do cache pre: {}", normalizedKey, e);
            // Ak zlyhá ExecutionException (zriedkavé), po?et pokusov sa stratí, ale app nespadne.
        }
    }


    public void loginSuccess(String key) {
        String normalizedKey = getNormalizedKey(key);
        attemptsCache.invalidate(normalizedKey);
        log.info("Počítadlo pokusov pre {} bolo resetované.", normalizedKey);
    }

    public boolean isBlocked(String key) {
        String normalizedKey = getNormalizedKey(key);

        try {
            int currentAttempts = attemptsCache.get(normalizedKey).get();
            return currentAttempts >= MAX_ATTEMPTS;

        } catch (ExecutionException e) {
            log.error("Chyba pri načítaní cache pre: {}", normalizedKey, e);
            return false;
        }
    }
}
