package com.patrickgrady.todayslists.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.patrickgrady.todayslists.Objects.DailyTasks;
import com.patrickgrady.todayslists.Objects.ListOfTasks;
import com.patrickgrady.todayslists.Objects.firebase.ListOfTasksFire;
import com.patrickgrady.todayslists.Objects.local.ListOfTasksLocal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

public class TasksManager extends TreeMap<String, ListOfTasks> implements UpdateManager.TimeSensitive {

    // static ref
    private static TasksManager instance;

    // shared member variables
    private DailyTasks dailyTasks;
    private ArrayList<String> order;
    private boolean localMode;

    // local storage member variables
    private SharedPreferences pref;
    private File directory;

    // firebase member variables
    private DatabaseReference dataRef;

    //#region constructor/set-up
    private TasksManager(boolean lm) {
        localMode = lm;
        order = new ArrayList<>();
    }

    public static TasksManager getInstance(boolean lm, Context ...c) {
        if (instance == null) {
            instance = new TasksManager(lm);
            instance.load(c);
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
        dailyTasks.clear();
        order.clear();

        if(localMode) {
            for (String filename : fileNames()) {
                new File(getDirectory(), filename).delete();
            }
        }
    }

    public void addList() {
        String key = UUID.randomUUID().toString();

        ListOfTasks t = new ListOfTasksFire(key);
        this.put(key, t);
        order.add(key);
    }

    public void remove(String key) {
        if(localMode && Arrays.asList(fileNames()).contains(key)) {
            new File(getDirectory(), key).delete();
        }
        super.remove(key);
        dailyTasks.remove(key);
        order.remove(key);
    }

    private void remove(String key, Iterator itr) {
        if(localMode && Arrays.asList(fileNames()).contains(key)) {
            new File(getDirectory(), key).delete();
        }
        super.remove(key);
        dailyTasks.remove(key);
        itr.remove();
    }

    // returns a list of today's tasks
    public ArrayList<String> getTasks() {
        refresh();
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
    public void update(long updateTimeMillis) {
        updateTodaysTasks(updateTimeMillis);
    }

    @Override
    public void refresh() {
        updateTodaysTasks(null);
    }

    // private helper/convenience methods

    // update today's chosen tasks list
    private void updateTodaysTasks(Long updateTimeMillis) {
        // clear out empty task lists
        for(Iterator itr = order.iterator(); itr.hasNext();) {
            String key = (String) itr.next();

            String currentTask = dailyTasks.getTask(key);
            ListOfTasks tasks = this.get(key);
            if(tasks != null) {
                if(tasks.isEmpty()) {
                    this.remove(key, itr);
                }
                else if(!tasks.contains(currentTask)) {
                    dailyTasks.updateTask(key, selectTask(tasks), updateTimeMillis);
                }
                else if(updateTimeMillis != null && dailyTasks.shouldUpdate(key, updateTimeMillis)) {
                    dailyTasks.updateTask(key, selectTask(tasks), updateTimeMillis);
                }
            }
        }
    }

    private String selectTask(ListOfTasks tasks) {
        // choose a random task
        int randomIndex = new Random().nextInt(tasks.size());
        return tasks.get(randomIndex);
    }
    //#endregion

    //#region handle type of storage
    private void load(Context ...c) {
        if(localMode) {
            if(c == null || c.length == 0 || c[0] == null) {
                throw new Error();
            }

            loadLocal(c[0]);
        }
        else {
            loadDatabase();
        }
    }
    //#endregion

    //#region firebase control
    private void loadDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("lists");
        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ListOfTasks tasks = dataSnapshot.getValue(ListOfTasksFire.class);
                TasksManager.this.put(dataSnapshot.getKey(), tasks);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //#endregion

    //#region local control
    private void loadLocal(Context c) {
        pref = c.getSharedPreferences("tasks",Context.MODE_PRIVATE);
        setDirectory(c);
        for(String filename : fileNames()) {
            ListOfTasksLocal tasks = new ListOfTasksLocal(getDirectory(), filename);
            tasks.loadFromFile();
            this.put(filename, tasks);
            order.add(filename);
        }
    }

    private String[] fileNames() {
        return getDirectory().list();
    }
    private File getDirectory() { return directory; }
    private void setDirectory(Context c) {
        directory = new File(c.getFilesDir(), "tasks");

        // creates the directory if not present yet
        directory.mkdir();
    }
    //#endregion

}
