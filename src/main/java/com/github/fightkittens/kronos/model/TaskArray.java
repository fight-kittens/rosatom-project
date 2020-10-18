package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TaskArray implements TaskResponse  {
    private SortedSet<TaskModel> tasks;

    public SortedSet<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(SortedSet<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public TaskArray() {
        this.tasks = new TreeSet<>();
    }

    public TaskArray(@JsonProperty("tasks") SortedSet<TaskModel> tasks) {
        this.tasks = tasks;
    }
}
