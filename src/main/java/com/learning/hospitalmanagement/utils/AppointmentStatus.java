package com.learning.hospitalmanagement.utils;

import lombok.Getter;

@Getter
public enum AppointmentStatus {
    PENDING("0"),
    CONFIRMED("1"),
    COMPLETED("2"),
    CANCELLED("3");

    private final String value;
    private final int index;

    AppointmentStatus(String value) {
        this.value = value;
        this.index = ordinal();
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public static AppointmentStatus fromValue(String value) {
        for (AppointmentStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AppointmentStatus value: " + value);
    }
}
