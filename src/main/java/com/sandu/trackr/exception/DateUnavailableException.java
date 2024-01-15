package com.sandu.trackr.exception;

import java.util.List;

public class DateUnavailableException extends RuntimeException{
    public List<String> dates;
    public DateUnavailableException(String message, List<String> dates) {
        super(message);
        this.dates = dates;
    }
}
