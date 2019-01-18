package com.patrickgrady.todayslists.Objects;

import java.util.HashMap;
import com.patrickgrady.todayslists.Objects.SimpleTime.TimeUnit;

public abstract class DailyTasks {
    private HashMap<String, DailyTask> tasks;

    //#region data manipulation for subclasses
    protected DailyTasks() {
        tasks = new HashMap<>();
    }

    protected DailyTask get(String key) { return tasks.get(key); }
    protected HashMap<String, DailyTask> getTaskMap() { return tasks; }
    protected void putTask(String key, DailyTask task) {
        tasks.put(key, task);
    }

    //#endregion

    //#region public interface
    public void clear() {
        tasks.clear();
    }

    public void remove(String key) {
        tasks.remove(key);
    }

    public String getTask(String key) {
        return tasks.containsKey(key) ? tasks.get(key).taskname : "";
    }

    public boolean shouldUpdate(String key, long timeMillis) {
        return tasks.containsKey(key) ? tasks.get(key).shouldUpdate(timeMillis) : false;
    }

    public abstract void updateTask(String key, String taskname, Long time);

    public abstract void updateFrequency(String key, TimeUnit unit, int numUnits);
    //#endregion

    protected class DailyTask {
        String taskname;
        long updated;
        TimeUnit timeUnit;
        int numUnits;

        public DailyTask() {}
        public DailyTask(String task, long created, TimeUnit unit, int nUnit) {
            taskname = task;
            updated = created;
            timeUnit = unit;
            numUnits = nUnit;
        }

        public DailyTask updateTask(String newTask, long updateTime) {
            DailyTask dt = new DailyTask();
            dt.taskname = newTask;
            dt.updated = updateTime;
            dt.timeUnit = this.timeUnit;
            dt.numUnits = this.numUnits;

            return dt;
        }

        public DailyTask updateTime(TimeUnit newUnit, int newNumUnits) {
            DailyTask dt = new DailyTask();
            dt.taskname = this.taskname;
            dt.updated = this.updated;
            dt.timeUnit = newUnit;
            dt.numUnits = newNumUnits;

            return dt;
        }

        public boolean shouldUpdate(long timeMillis) {
            return new Day(timeMillis).isAfter(new Day(updated));
        }

        public void update(String task, long updateTime) {
            taskname = task;
            updated = updateTime;
        }

        public String getTaskForFile(String delim) {
            delim = delim == null ? "," : delim;
            return taskname + delim + updated + delim + timeUnit.toString() + delim + numUnits;
        }
    }
}
