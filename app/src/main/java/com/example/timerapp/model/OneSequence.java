package com.example.timerapp.model;

public class OneSequence {
    public String name;
    public long start_timestamp = 0, end_timestamp = 0;

    public OneSequence(String name) {
        this.name = name;
    }

    public OneSequence(String name, long start_ts) {
        this.name = name;
        this.start_timestamp = start_ts;
    }

    public String toString(String sep) {
        return String.format("%s%s%d%s%d", name, sep, start_timestamp, sep, end_timestamp);
    }
    public String[] toStringArray(){
        return new String[]{name, String.valueOf(start_timestamp), String.valueOf(end_timestamp)};
    }
}
