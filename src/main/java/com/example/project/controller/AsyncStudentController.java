package com.example.project.controller;

import com.example.project.service.AsyncStudentService;
import com.example.project.service.RaceConditionDemoService;
import com.example.project.service.AsyncTaskStorage;
import com.example.project.model.AsyncTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/students")
public class AsyncStudentController {

    private final AsyncStudentService studentStatsService;
    private final RaceConditionDemoService raceConditionService;
    private final AsyncTaskStorage taskStorage;

    public AsyncStudentController(AsyncStudentService studentStatsService,
                                  RaceConditionDemoService raceConditionService,
                                  AsyncTaskStorage taskStorage) {
        this.studentStatsService = studentStatsService;
        this.raceConditionService = raceConditionService;
        this.taskStorage = taskStorage;
    }

    @GetMapping("/start")
    public Map<String, String> collectStatistics() {
        String taskId = UUID.randomUUID().toString();
        studentStatsService.initTask(taskId);
        studentStatsService.processStudentReportAsync(taskId);
        return Map.of(
                "taskId", taskId,
                "message", "Сбор статистики запущен..."
        );
    }

    @GetMapping("/status/{taskId}")
    public Map<String, Object> getStatus(@PathVariable String taskId) {
        AsyncTask task = taskStorage.getTask(taskId);
        if (task == null) {
            return Map.of("error", "Задача не найдена");
        }
        return Map.of(
                "status", task.getStatus(),
                "progress", task.getProgress() != null ? task.getProgress() : 0,
                "result", task.getResult() != null ? task.getResult() : "В обработке..."
        );
    }

    @GetMapping("/concurrency-test")
    public String testConcurrency() throws InterruptedException {
        raceConditionService.runAllDemos();
        return "Тестирование многопоточности завершено. Результаты в логах консоли.";
    }
}