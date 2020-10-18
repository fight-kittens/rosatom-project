package com.github.fightkittens.kronos.model;

public class PenaltyValue implements TaskResponse {
    private long value;

    public PenaltyValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
