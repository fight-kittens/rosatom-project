package com.github.fightkittens.kronos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TaskMoves implements TaskResponse {
    private List<TaskMove> moves;

    public TaskMoves(@JsonProperty("moves") List<TaskMove> moves) {
        this.moves = moves;
    }

    public List<TaskMove> getMoves() {
        return moves;
    }

    public void setMoves(List<TaskMove> moves) {
        this.moves = moves;
    }
}
