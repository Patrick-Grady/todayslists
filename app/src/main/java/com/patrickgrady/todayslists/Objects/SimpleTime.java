package com.patrickgrady.todayslists.Objects;

public class SimpleTime {
    TimeUnit unit;
    int numUnits;

    public enum TimeUnit {
        Day,
        Week,
        Month


    }

    public static TimeUnit map(String unit) {
        switch(unit.toLowerCase()) {
            case "week":
                return TimeUnit.Week;
            case "month":
                return TimeUnit.Month;
            default:
                return TimeUnit.Day;
        }
    }
}
