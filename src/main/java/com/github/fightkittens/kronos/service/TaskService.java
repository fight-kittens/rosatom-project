package com.github.fightkittens.kronos.service;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.model.TaskMove;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TaskService {
    @Autowired
    private TaskRepository repository;
    private long currentPrice;

    public void append(List<TaskMove> moves) {
        try {
            long penalty = calculate(moves);
            for (TaskMove move : moves) {
                Task task = repository.findById(move.getTaskId()).orElse(null);
                if (task != null) {
                    task.setDuration(move.getNewDuration());
                    task.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(move.getNewStartDate()));
                    repository.save(task);
                }
            }
            repository.flush();
            currentPrice += penalty;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long calculate(List<TaskMove> moves) {
        try {
            long penalty = 0;
            for (TaskMove move : moves) {
                Task task = repository.findById(move.getTaskId()).orElse(null);
                if (task != null) {
                    if (task.getReduceDurationPrice() == -1 && task.getMinDuration() == 0 && task.getDuration() == 0) {
                        return -1;
                    }
                    long durationDays = task.getDuration() - move.getNewDuration();
                    long startDays = ChronoUnit.DAYS.between(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(move.getNewStartDate()).toInstant(),
                            task.getStartDate().toInstant());
                    if (startDays < 0) {
                        if (task.getShiftEarlierPrice() == -1) {
                            return -1;
                        } else {
                            penalty += (-startDays) * task.getShiftEarlierPrice();
                        }
                    } else {
                        if (task.getShiftLaterPrice() == -1) {
                            return -1;
                        } else {
                            penalty += startDays * task.getShiftLaterPrice();
                        }
                    }
                    if (task.getReduceDurationPrice() == -1) {
                        return -1;
                    } else {
                        penalty += durationDays * task.getReduceDurationPrice();
                    }
                }
            }
            return penalty;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }
}
