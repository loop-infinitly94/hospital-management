package com.learning.hospitalmanagement.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppointmentFreeSlots {
    private List<String> timeSlots;
    public AppointmentFreeSlots() {
        timeSlots = new ArrayList<>();
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void addSlots(String slot) {
        this.timeSlots.add(slot);
    }
}
