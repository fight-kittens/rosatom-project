package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskMove implements TaskResponse {
    private int taskId;
    private int newDuration;
    private String newStartDate;

    public TaskMove(@JsonProperty("taskId") int taskId,
                    @JsonProperty("newDuration") int newDuration,
                    @JsonProperty("newStartDate") String newStartDate) {
        this.taskId = taskId;
        this.newDuration = newDuration;
        this.newStartDate = newStartDate;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getNewDuration() {
        return newDuration;
    }

    public void setNewDuration(int newDuration) {
        this.newDuration = newDuration;
    }

    public String getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(String newStartDate) {
        this.newStartDate = newStartDate;
    }
}
