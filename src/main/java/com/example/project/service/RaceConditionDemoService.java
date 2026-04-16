package com.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RaceConditionDemoService {

    private int unsafeStudentCounter = 0;
    private int syncStudentCounter = 0;
    private final AtomicInteger atomicStudentCounter = new AtomicInteger(0);

    private static final int THREAD_COUNT = 50;
    private static final int ITERATIONS = 1000;

    public Map<String, Object> runAllDemos() throws InterruptedException {
        unsafeStudentCounter = 0;
        syncStudentCounter = 0;
        atomicStudentCounter.set(0);

        int expected = THREAD_COUNT * ITERATIONS;
        log.info("Запуск теста: {} потоков...", THREAD_COUNT);

        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        unsafeStudentCounter++;
                        incrementSynchronized();
                        atomicStudentCounter.incrementAndGet();
                    }
                });
            }
            executor.shutdown();
            boolean finishedCleanly = executor.awaitTermination(1, TimeUnit.MINUTES);
            if (!finishedCleanly) {
                log.warn("Внимание: потоки не завершились вовремя!");
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("expected", expected);
        response.put("unsafeResult", unsafeStudentCounter);
        response.put("syncResult", syncStudentCounter);
        response.put("atomicResult", atomicStudentCounter.get());
        response.put("status", "COMPLETED");

        return response;
    }

    private synchronized void incrementSynchronized() {
        syncStudentCounter++;
    }
}