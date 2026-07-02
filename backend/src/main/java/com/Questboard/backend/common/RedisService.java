package com.Questboard.backend.common;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    public void setToken(String keyPrefix, String username, String token, long expirySecond) {
        try {
            String key = key(keyPrefix, username);
            redisTemplate.opsForValue().set(key, token, expirySecond, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting token in Redis for user '{}': {}", username, e.getMessage());
        }
    }

    public String getToken(String keyPrefix, String username) {
        try {
            String key = key(keyPrefix, username);
            Object token = redisTemplate.opsForValue().get(key);
            return token != null ? token.toString() : null;
        } catch (Exception e) {
            log.error("Error getting token from Redis for user '{}': {}", username, e.getMessage());
            return null;
        }
    }

    public void deleteToken(String keyPrefix, String username) {
        try {
            String key = key(keyPrefix, username);
            redisTemplate.delete(key);
            log.info("Token deleted for user '{}'", username);
        } catch (Exception e) {
            log.error("Error deleting token from Redis for user '{}': {}", username, e.getMessage());
        }
    }

    public void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Key '{}' deleted from Redis", key);
        } catch (Exception e) {
            log.error("Error deleting key '{}' from Redis: {}", key, e.getMessage());
        }
    }

    public boolean tokenExists(String keyPrefix, String username) {
        try {
            String key = key(keyPrefix, username);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("🔴 Error checking token in Redis: {}", e.getMessage());
            return false;
        }

    }

    public String key(String Prefix, String Suffix) {
        return Prefix + ":" + Suffix;
    }
}
