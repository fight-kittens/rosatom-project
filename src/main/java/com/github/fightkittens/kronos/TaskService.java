package com.github.fightkittens.kronos;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskService {
    @Autowired
    private TaskRepository repository;
    private int maxAllowedPenalty;

    public void reorganize(int scheduleId) {
        List<Task> tasks = new ArrayList<>(repository.filterBySchedule(scheduleId));

    }

    public void setMaxPenalty(int penalty) {
        maxAllowedPenalty = penalty;
    }

    public int getMaxPenalty() {
        return maxAllowedPenalty;
    }
}
