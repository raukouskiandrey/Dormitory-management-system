package com.example.project.service;

import com.example.project.model.AsyncTask;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AsyncTaskStorage {
    private final Map<String, AsyncTask> taskStatuses = new ConcurrentHashMap<>();

    public void saveTask(AsyncTask task) {
        taskStatuses.put(task.getTaskId(), task);
    }

    public AsyncTask getTask(String taskId) {
        return taskStatuses.get(taskId);
    }

}