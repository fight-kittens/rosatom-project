package com.github.fightkittens.kronos.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fightkittens.kronos.entities.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TaskModel implements TaskResponse, Comparable<TaskModel> {
    private int id;
    private String name;
    private String startDate;
    private int shiftEarlierPrice;
    private int shiftLaterPrice;
    private int duration;
    private int minDuration;
    private int reduceDurationPrice;
    private Set<Integer> children;
    private int scheduleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getShiftEarlierPrice() {
        return shiftEarlierPrice;
    }

    public void setShiftEarlierPrice(int shiftEarlierPrice) {
        this.shiftEarlierPrice = shiftEarlierPrice;
    }

    public int getShiftLaterPrice() {
        return shiftLaterPrice;
    }

    public void setShiftLaterPrice(int shiftLaterPrice) {
        this.shiftLaterPrice = shiftLaterPrice;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getReduceDurationPrice() {
        return reduceDurationPrice;
    }

    public void setReduceDurationPrice(int reduceDurationPrice) {
        this.reduceDurationPrice = reduceDurationPrice;
    }

    public Set<Integer> getChildren() {
        return children;
    }

    public void setChildren(Set<Integer> children) {
        this.children = children;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public TaskModel() { }

    @JsonCreator
    public TaskModel(@JsonProperty("name") String name,
                     @JsonProperty("startDate") String startDate,
                     @JsonProperty("shiftEarlierPrice") int shiftEarlierPrice,
                     @JsonProperty("shiftLaterPrice") int shiftLaterPrice,
                     @JsonProperty("duration") int duration,
                     @JsonProperty("minDuration") int minDuration,
                     @JsonProperty("reduceDurationPrice") int reduceDurationPrice,
                     @JsonProperty("childrenTasks") Set<Integer> children,
                     @JsonProperty("scheduleId") int scheduleId) {
        this.name = name;
        this.startDate = startDate;
        this.shiftEarlierPrice = shiftEarlierPrice;
        this.shiftLaterPrice = shiftLaterPrice;
        this.duration = duration;
        this.minDuration = minDuration;
        this.reduceDurationPrice = reduceDurationPrice;
        this.children = children;
        this.scheduleId = scheduleId;
    }

    public TaskModel(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.startDate = task.getStartDate().toString();
        this.shiftEarlierPrice = task.getShiftEarlierPrice();
        this.shiftLaterPrice = task.getShiftLaterPrice();
        this.duration = task.getDuration();
        this.minDuration = task.getMinDuration();
        this.reduceDurationPrice = task.getReduceDurationPrice();
        this.children = task.getChildren().stream().map(Task::getId).collect(Collectors.toSet());
        this.scheduleId = task.getScheduleId();
    }

    @Override
    public int compareTo(TaskModel o) {
        return (Integer.compare(id, o.getId()));
    }
}
