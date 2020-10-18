package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fightkittens.kronos.entities.Task;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Date;

public class ScheduleParams implements TaskResponse {
    private int scheduleId;
    private int durationCost;
    private String startDate;
    private String endDate;

    public ScheduleParams(@JsonProperty("id") int scheduleId,
                          @JsonProperty("durationCost") int durationCost,
                          @JsonProperty("startDate") String startDate,
                          @JsonProperty("endDate") String endDate) {
        this.scheduleId = scheduleId;
        this.durationCost = durationCost;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getDurationCost() {
        return durationCost;
    }

    public void setDurationCost(int durationCost) {
        this.durationCost = durationCost;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ScheduleParams(Collection<Task> tasks) {
        this.scheduleId = tasks.stream().findFirst().get().getScheduleId();
        Date startDate = null;
        Date endDate = null;
        int durationCost = 0;
        for (Task task : tasks) {
            if (startDate == null || startDate.after(task.getStartDate())) {
                startDate = task.getStartDate();
            }
            Date taskEndDate = Date.from(task.getStartDate().toInstant().plus(Duration.ofDays(task.getDuration())));
            if (endDate == null || endDate.before(taskEndDate)) {
                endDate = taskEndDate;
            }
            durationCost += task.getDuration();
        }
        this.durationCost = durationCost;
        this.startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        this.endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
    }
}
