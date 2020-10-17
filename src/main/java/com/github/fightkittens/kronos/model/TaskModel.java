package com.github.fightkittens.kronos.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fightkittens.kronos.entities.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskModel implements TaskResponse {
    private int id;
    private String name;
    private String startDate;
    private int shiftEarlierPrice;
    private int shiftLaterPrice;
    private int duration;
    private int minDuration;
    private int reduceDurationPrice;
    private Integer parentTask;
    private List<Integer> children;
    private List<Integer> connected;
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

    public Integer getParentTask() {
        return parentTask;
    }

    public void setParentTask(int parentTask) {
        this.parentTask = parentTask;
    }

    public void setParentTask(Integer parentTask) {
        this.parentTask = parentTask;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }

    public List<Integer> getConnected() {
        return connected;
    }

    public void setConnected(List<Integer> connected) {
        this.connected = connected;
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
                     @JsonProperty("parentTask") Integer parentTask,
                     @JsonProperty("childrenTasks") List<Integer> children,
                     @JsonProperty("connectedTasks") List<Integer> connected,
                     @JsonProperty("scheduleId") int scheduleId) {
        this.name = name;
        this.startDate = startDate;
        this.shiftEarlierPrice = shiftEarlierPrice;
        this.shiftLaterPrice = shiftLaterPrice;
        this.duration = duration;
        this.minDuration = minDuration;
        this.reduceDurationPrice = reduceDurationPrice;
        this.parentTask = parentTask;
        this.children = children;
        this.connected = connected;
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
        this.parentTask = (task.getParent() == null ? null : task.getParent().getId());
        this.children = task.getChildren().stream().map(Task::getId).collect(Collectors.toList());
        this.connected = task.getConnected().stream().map(Task::getId).collect(Collectors.toList());
        this.scheduleId = task.getScheduleId();
    }
}
