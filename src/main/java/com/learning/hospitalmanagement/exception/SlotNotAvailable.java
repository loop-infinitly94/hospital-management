package com.learning.hospitalmanagement.exception;

public class SlotNotAvailable extends RuntimeException{
    public SlotNotAvailable() {
        super();
    }

    public SlotNotAvailable(String message) {
        super(message);
    }
}
