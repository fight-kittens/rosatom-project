package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashSet;
import java.util.Set;

public class TaskArray implements TaskResponse  {
    private Set<TaskModel> tasks;

    public Set<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public TaskArray() {
        this.tasks = new LinkedHashSet<>();
    }

    public TaskArray(@JsonProperty("tasks") Set<TaskModel> tasks) {
        this.tasks = tasks;
    }
}
