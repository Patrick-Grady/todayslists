package com.patrickgrady.todayslists.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.patrickgrady.todayslists.Managers.Objects.Day;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateManager extends ArrayList<UpdateManager.TimeSensitive> {

    private static UpdateManager instance;
    private SharedPreferences tPref;

    private UpdateManager(Context c, TimeSensitive ...ob) {
        tPref = c.getSharedPreferences("time", Context.MODE_PRIVATE);
        add(ob);
    }

    public static UpdateManager getInstance() {
        return instance;
    }

    public static void init(Context c, TimeSensitive ...ob) {
        if(instance == null) {
            instance = new UpdateManager(c, ob);
            instance.updateListeners();
        }
    }

    public void add(TimeSensitive ...ob) {
        this.addAll(Arrays.asList(ob));
    }

    public void updateListeners() {
        if(shouldUpdateTasks()) {
            for (TimeSensitive t : this) {
                t.update();
            }
        }
        else {
            for(TimeSensitive t : this) {
                t.refresh();
            }
        }
    }

    // determines whether tasks should be updated based on day
    private boolean shouldUpdateTasks() {
        long then = tPref.getLong("time", 0);
        long now = System.currentTimeMillis();
        Day savedDay = new Day(then);
        Day today = new Day(now);

        boolean shouldUpdate = today.isAfter(savedDay);

        if(shouldUpdate)
            tPref.edit().putLong("time", now).commit();

        return shouldUpdate;
    }

    public interface TimeSensitive {
        void update();
        void refresh();
    }
}
