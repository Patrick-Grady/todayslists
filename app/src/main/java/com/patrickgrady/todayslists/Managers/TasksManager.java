package com.patrickgrady.todayslists.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.patrickgrady.todayslists.Managers.Objects.ListOfTasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

public class TasksManager extends TreeMap<String, ListOfTasks> implements UpdateManager.TimeSensitive {

    private static TasksManager instance;
    Context context;
    ArrayList<String> order;
    SharedPreferences pref;

    //#region constructor/set-up
    private TasksManager(Context c) {
        context = c;
        order = new ArrayList<>();
        pref = context.getSharedPreferences("tasks",Context.MODE_PRIVATE);
    }

    public static TasksManager getInstance(Context c) {
        if (instance == null) {
            instance = new TasksManager(c);
            instance.load();
        }

        return instance;
    }

    public static TasksManager getInstance() throws Error {
        if (instance == null) {
            throw new Error();
        }

        return instance;
    }
    //#endregion constructor/set-up

    //#region change data
    @Override
    public void clear() {
        super.clear();
        pref.edit().clear().commit();
        order.clear();
        for(String filename : fileNames()) {
            new File(getDirectory(), filename).delete();
        }
        updateTodaysTasks();
    }

    public void addList() {
        List<String> tasks = new ArrayList<>();
        String key = UUID.randomUUID().toString();

        ListOfTasks t = new ListOfTasks(getDirectory(), key);
        t.setAll(tasks);
        this.put(key, t);
        order.add(key);
    }

    public void remove(String key) {
        if(Arrays.asList(fileNames()).contains(key)) {
            new File(getDirectory(), key).delete();
        }
        super.remove(key);
        pref.edit().remove(key).commit();
        order.remove(key);
        updateTodaysTasks();
    }

    private void remove(String key, Iterator itr) {
        if(Arrays.asList(fileNames()).contains(key)) {
            new File(getDirectory(), key).delete();
        }
        super.remove(key);
        pref.edit().remove(key).commit();
        itr.remove();
    }

    // returns a list of today's tasks
    public ArrayList<String> getTasks() {
        updateTodaysTasks();
        return order;
    }

    public String getTask(String key) {
        return pref.getString(key, "");
    }

    public void move(int oldPosition, int newPosition) {
        String key = order.remove(oldPosition);
        order.add(newPosition, key);
    }
    //#endregion

    //#region update daily tasks
    @Override
    public void update() {
        pref.edit().clear().commit();
        updateTodaysTasks();
    }

    @Override
    public void refresh() {
        updateTodaysTasks();
    }

    // private helper/convenience methods

    // update today's chosen tasks list
    private void updateTodaysTasks() {
        // clear out empty task lists
        for(Iterator itr = order.iterator(); itr.hasNext();) {
            String key = (String) itr.next();

            String currentTask = pref.getString(key, "");
            ListOfTasks tasks = this.get(key);
            if(tasks != null) {
                if(tasks.isEmpty()) {
                    this.remove(key, itr);
                }
                else if(!tasks.contains(currentTask)) {
                    updateDailyTask(key);
                }
            }
        }
    }

    // convenience method
    private void updateDailyTask(String key) {
        System.out.println("in1: " + key);
        updateDailyTask(this.get(key));
    }

    // update a single task for today
    private void updateDailyTask(ListOfTasks tasks) {
        // choose a random task
        String task = selectTask(tasks);

        // store that task for today
        pref.edit().putString(tasks.getFilename(), task).commit();
    }

    private String selectTask(ListOfTasks tasks) {
        // choose a random task
        int randomIndex = new Random().nextInt(tasks.size());
        return tasks.get(randomIndex);
    }
    //#endregion

    //#region low level file stuff
    private void load() {
        for(String filename : fileNames()) {
            ListOfTasks tasks = new ListOfTasks(getDirectory(), filename);
            tasks.loadFromFile();
            this.put(filename, tasks);
            order.add(filename);
        }
    }

    private String[] fileNames() {
        return getDirectory().list();
    }

    private File getDirectory() {
        File directory = new File(context.getFilesDir(), "tasks");

        // creates the directory if not present yet
        directory.mkdir();

        return directory;
    }
    //#endregion

}
