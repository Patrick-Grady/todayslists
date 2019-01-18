package com.patrickgrady.todayslists.Objects.local;

import com.patrickgrady.todayslists.Objects.DailyTasks;
import com.patrickgrady.todayslists.Objects.SimpleTime;
import com.patrickgrady.todayslists.Objects.SimpleTime.TimeUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DailyTaskLocal extends DailyTasks {
    final String PATH = "daily";
    final String DELIM = "```";
    File tasks;

    public DailyTaskLocal(File root) {
        super();
        tasks = new File(root, PATH);

        try {
            Scanner input = new Scanner(tasks);
            while(input.hasNextLine()) {
                String []fields = input.nextLine().split(DELIM);

                if(fields.length == 5) {
                    String key = fields[0];
                    String name = fields[1];
                    long updateTime = Long.parseLong(fields[2]);
                    TimeUnit unit = SimpleTime.map(fields[3]);
                    int numUnits = Integer.parseInt(fields[4]);

                    super.putTask(key, new DailyTask(name, updateTime, unit, numUnits));
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeTasksToFile() {
        try {
            PrintWriter writer = new PrintWriter(tasks);
            for (String key : getTaskMap().keySet())
                writer.println(key + DELIM + get(key).getTaskForFile(DELIM));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putTask(String key, DailyTask task) {
        super.putTask(key, task);
        writeTasksToFile();
    }

    @Override
    public void clear() {
        tasks.delete();
        super.clear();
    }

    @Override
    public void remove(String key) {
        super.remove(key);
        writeTasksToFile();
    }

    @Override
    public void updateTask(String key, String task, Long time) {
        time = (time == null) ? System.currentTimeMillis() : time;

        DailyTask updatedTask;
        if(super.get(key) == null) {
            updatedTask = new DailyTask(task, time, TimeUnit.Day, 1);
        }
        else {
            updatedTask = super.get(key).updateTask(task, time);
        }
        putTask(key, updatedTask);
    }

    @Override
    public void updateFrequency(String key, TimeUnit unit, int numUnits) {
        DailyTask newTask = super.get(key).updateTime(unit, numUnits);
        putTask(key, newTask);
    }

}
