package com.learning.hospitalmanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkingHours {
    public String date;
    public String start;
    public String end;
}
