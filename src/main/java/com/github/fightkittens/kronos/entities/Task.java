package com.github.fightkittens.kronos.entities;

import com.github.fightkittens.kronos.model.TaskModel;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Task implements Comparable<Task> {
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;
    private String name;
    private Date startDate;
    private int shiftEarlierPrice;
    private int shiftLaterPrice;
    private int duration;
    private int minDuration;
    private int reduceDurationPrice;
    @ManyToMany(targetEntity = Task.class, cascade = CascadeType.ALL)
    @JoinTable(name = "task_relations",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id"))
    private Set<Task> children;
    private int scheduleId;

    public Task(TaskModel taskModel) throws ParseException {
        this.name = taskModel.getName();
        this.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(taskModel.getStartDate());
        this.shiftEarlierPrice = taskModel.getShiftEarlierPrice();
        this.shiftLaterPrice = taskModel.getShiftLaterPrice();
        this.duration = taskModel.getDuration();
        this.minDuration = taskModel.getMinDuration();
        this.reduceDurationPrice = taskModel.getReduceDurationPrice();
        this.children = new TreeSet<>();
        this.scheduleId = taskModel.getScheduleId();
    }

    public Task() {

    }

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
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

    public Set<Task> getChildren() {
        return children;
    }

    public void setChildren(Set<Task> children) {
        this.children = children;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void addChild(Task task) {
        this.children.add(task);
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(id, o.getId());
    }
}
