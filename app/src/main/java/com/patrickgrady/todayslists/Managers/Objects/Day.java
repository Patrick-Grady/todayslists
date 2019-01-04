package com.patrickgrady.todayslists.Managers.Objects;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Day {
    Date date;
    LocalDate localDate;

    public Day(long milliseconds) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localDate = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = sdf.parse(sdf.format(new Date(milliseconds)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAfter(Day day) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return localDate.isAfter(day.localDate);
        }
        else {
            return date.after(day.date);
        }
    }

    @Override
    public String toString() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return localDate.toString();
        }
        else {
            return date.toString();
        }
    }
}
