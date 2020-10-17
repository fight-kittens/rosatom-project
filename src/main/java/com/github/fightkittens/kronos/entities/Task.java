package com.github.fightkittens.kronos.entities;

import com.github.fightkittens.kronos.model.TaskModel;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Task {
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
    @JoinTable(name = "task_relation", joinColumns = {
            @JoinColumn(name = "child_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "parent_id", referencedColumnName = "id")})
    @ManyToOne(targetEntity = Task.class, cascade = CascadeType.ALL)
    private Task parent;
    @OneToMany(targetEntity = Task.class)
    private Set<Task> children;
    @JoinTable(name = "stream_relation", joinColumns = {
            @JoinColumn(name = "task_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "pair_id", referencedColumnName = "id")})
    @ManyToMany(targetEntity = Task.class, cascade = CascadeType.ALL)
    private Set<Task> connected;
    private int scheduleId;

    public Task(TaskModel taskModel, Task parent) throws ParseException {
        this.name = taskModel.getName();
        this.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(taskModel.getStartDate());
        this.shiftEarlierPrice = taskModel.getShiftEarlierPrice();
        this.shiftLaterPrice = taskModel.getShiftLaterPrice();
        this.duration = taskModel.getDuration();
        this.minDuration = taskModel.getMinDuration();
        this.reduceDurationPrice = taskModel.getReduceDurationPrice();
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
        this.children = new LinkedHashSet<>();
        this.connected = new LinkedHashSet<>();
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

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public Set<Task> getChildren() {
        return children;
    }

    public void setChildren(Set<Task> children) {
        this.children = children;
    }

    public Set<Task> getConnected() {
        return connected;
    }

    public void setConnected(Set<Task> connected) {
        this.connected = connected;
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

    public void addConnected(Task task) {
        this.connected.add(task);
    }
}
