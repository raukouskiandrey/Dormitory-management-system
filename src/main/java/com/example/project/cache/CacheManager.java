package com.example.project.cache;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class CacheManager {
    private final Map<CacheKey, Object> storage = new HashMap<>();

    @SuppressWarnings("unchecked")
    public synchronized <T> T computeIfAbsent(CacheKey key, Supplier<T> supplier) {
        if (storage.containsKey(key)) {
            System.out.println("--- [CACHE] Данные взяты из кэша (Key: " + key.methodName() + ") ---");
            return (T) storage.get(key);
        }

        System.out.println("--- [DB] Данные взяты из БД (Key: " + key.methodName() + ") ---");
        T result = supplier.get();
        storage.put(key, result);
        return result;
    }

    public synchronized void invalidate(Class<?>... entityClasses) {
        var classesList = Arrays.asList(entityClasses);

        boolean removed = storage.keySet().removeIf(key -> classesList.contains(key.entityClass()));

        if (removed) {
            System.out.println("--- [INVALIDATE] Кэш для сущностей " + classesList + " успешно очищен ---");
        }
    }

    public synchronized void clearAll() {
        storage.clear();
        System.out.println("--- [CACHE] Весь кэш полностью очищен ---");
    }
}