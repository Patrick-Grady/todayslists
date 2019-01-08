package com.patrickgrady.todayslists.Objects;

import java.util.HashMap;

public abstract class DailyTasks {
    HashMap<String, DailyTask> tasks;

    public abstract void clear();
    public abstract void remove(String key);

    public String getTask(String key) {
        return tasks.containsKey(key) ? tasks.get(key).taskname : "";
    }

    public boolean shouldUpdate(String key, long timeMillis) {
        return tasks.containsKey(key) ? tasks.get(key).shouldUpdate(timeMillis) : false;
    }

    public void updateTask(String key, String taskname, Long time) {
        time = (time == null) ? System.currentTimeMillis() : time;

        if(tasks.containsKey(key)) {
            tasks.get(key).update(taskname, time);
        }
    }

    public class DailyTask {
        String taskname;
        long updated;
        String timeUnit;
        int numUnits;

        boolean shouldUpdate(long timeMillis) {
            return new Day(timeMillis).isAfter(new Day(updated));
        }

        void update(String task, long updateTime) {
            taskname = task;
            updated = updateTime;
        }
    }
}
