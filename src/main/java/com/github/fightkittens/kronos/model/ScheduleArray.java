package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScheduleArray implements TaskResponse {
    private List<ScheduleParams> schedules;

    public ScheduleArray(@JsonProperty("schedules") List<ScheduleParams> schedules) {
        this.schedules = schedules;
    }

    public List<ScheduleParams> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleParams> schedules) {
        this.schedules = schedules;
    }
}
