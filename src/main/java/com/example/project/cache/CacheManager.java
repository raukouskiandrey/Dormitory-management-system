package com.example.project.cache;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);
    private final Map<CacheKey, Object> storage = new HashMap<>();

    @SuppressWarnings("unchecked")
    public synchronized <T> T computeIfAbsent(CacheKey key, Supplier<T> supplier) {
        if (storage.containsKey(key)) {
            logger.info("--- [CACHE] Данные взяты из кэша (Key: {}) ---", key.methodName());
            return (T) storage.get(key);
        }

        logger.info("--- [DB] Данные взяты из БД (Key: {}) ---", key.methodName());
        T result = supplier.get();
        storage.put(key, result);
        return result;
    }

    public synchronized void invalidate(Class<?>... entityClasses) {
        var classesList = Arrays.asList(entityClasses);

        boolean removed = storage.keySet().removeIf(key -> classesList.contains(key.entityClass()));

        if (removed) {
            logger.info("--- [INVALIDATE] Кэш для сущностей {} успешно очищен ---", classesList);
        }
    }
}