package com.example.project.service;

import com.example.project.model.AsyncTask;
import com.example.project.model.enums.AsyncTaskStatus;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AsyncStudentService {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncStudentService.class);
    private static final int SIMULATION_DELAY_MS = 4000;
    private static final int PROGRESS_30 = 30;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_90 = 90;
    private static final int PROGRESS_100 = 100;

    private final StudentRepository studentRepository;
    private final ViolationRepository violationRepository;
    private final AsyncTaskStorage taskStorage;

    public AsyncStudentService(StudentRepository studentRepository,
                               ViolationRepository violationRepository,
                               AsyncTaskStorage taskStorage) {
        this.studentRepository = studentRepository;
        this.violationRepository = violationRepository;
        this.taskStorage = taskStorage;
    }

    public void initTask(String taskId) {
        AsyncTask task = new AsyncTask();
        task.setTaskId(taskId);
        task.setStatus(AsyncTaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.now());
        task.setProgress(0);
        taskStorage.saveTask(task);
    }

    @Async
    public void processStudentReportAsync(String taskId) {
        LOG.info("Запуск фонового отчета (Task ID: {})", taskId);
        AsyncTask task = taskStorage.getTask(taskId);

        if (task == null) {
            LOG.error("Задача {} не найдена", taskId);
            return;
        }

        try {
            task.setProgress(PROGRESS_30);
            Thread.sleep(SIMULATION_DELAY_MS);

            long studentsCount = studentRepository.count();
            task.setProgress(PROGRESS_60);
            Thread.sleep(SIMULATION_DELAY_MS);

            long violationsCount = violationRepository.count();
            task.setProgress(PROGRESS_90);
            Thread.sleep(SIMULATION_DELAY_MS);

            String result = String.format(
                    "Найдено студентов - %d , нарушений - %d",
                    studentsCount,
                    violationsCount
            );

            task.setResult(result);
            task.setStatus(AsyncTaskStatus.COMPLETED);
            task.setProgress(PROGRESS_100);
            task.setEndTime(LocalDateTime.now());
            LOG.info("Отчет готов (Task ID: {})", taskId);

        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            task.setStatus(AsyncTaskStatus.CANCELLED);
            task.setResult("Задача прервана");
        } catch (Exception e) {
            task.setStatus(AsyncTaskStatus.FAILED);
            task.setResult("Ошибка: " + e.getMessage());
            LOG.error("Критическая ошибка", e);
        } finally {
            taskStorage.saveTask(task);
        }
    }
}