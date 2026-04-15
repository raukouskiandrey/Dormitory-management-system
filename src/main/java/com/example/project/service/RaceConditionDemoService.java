package com.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public void runAllDemos() throws InterruptedException {
        unsafeStudentCounter = 0;
        syncStudentCounter = 0;
        atomicStudentCounter.set(0);

        log.info("Запуск теста: 50 потоков одновременно инкрементируют счетчик студентов.");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

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
        executor.awaitTermination(1, TimeUnit.MINUTES);

        log.info("=== РЕЗУЛЬТАТЫ ПОДСЧЕТА СТУДЕНТОВ ===");
        log.info("Ожидалось: {}", THREAD_COUNT * ITERATIONS);
        log.info("Небезопасный результат (потери данных): {}", unsafeStudentCounter);
        log.info("Синхронизированный результат: {}", syncStudentCounter);
        log.info("Атомарный результат: {}", atomicStudentCounter.get());
    }

    private synchronized void incrementSynchronized() {
        syncStudentCounter++;
    }
}