package eu.invouk.nexuschunk.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5;
    private final LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginFailed(String key) {
        int atttempts;

        try {
            atttempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            atttempts = 0;
        }

        atttempts++;
        attemptsCache.put(key, atttempts);
    }

    public void loginSuccess(String key) {
        attemptsCache.invalidate(key);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPTS;
        }catch (ExecutionException e) {
            return false;
        }
    }
}
