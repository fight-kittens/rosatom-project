package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashSet;
import java.util.Set;

public class TaskIdArray implements TaskResponse {
    private Set<Integer> tasks;

    public Set<Integer> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Integer> tasks) {
        this.tasks = tasks;
    }

    public TaskIdArray() {
        this.tasks = new LinkedHashSet<>();
    }

    public TaskIdArray(@JsonProperty("tasks") Set<Integer> tasks) {
        this.tasks = tasks;
    }
}
