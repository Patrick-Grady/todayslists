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
import com.patrickgrady.todayslists.Objects.local.DailyTaskLocal;
import com.patrickgrady.todayslists.Objects.local.ListOfTasksLocal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private File directory, listDirectory;

    // firebase member variables
    private DatabaseReference childRef, dataRef;

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
            for (File file : listsFiles()) {
                file.delete();
            }
        }
        else {
            childRef.removeValue();
        }
    }

    public void addList() {
        String key = UUID.randomUUID().toString();

        ListOfTasks t;
        if(localMode) {
            t = new ListOfTasksLocal(listDirectory, key);
            this.put(key, t);
            refresh();
        }
        else {
            t = new ListOfTasksFire(key);
            childRef.child(key).setValue(t);
        }

        order.add(key);
    }

    public void remove(String key) {
        if(localMode) {
            new File(listDirectory, key).delete();
            super.remove(key);
        }
        else {
            childRef.child(key).removeValue();
        }

        dailyTasks.remove(key);
        order.remove(key);
    }

    private void remove(String key, Iterator itr) {
        if(localMode) {
            new File(listDirectory, key).delete();
            super.remove(key);
        }
        else {
            childRef.child(key).removeValue();
        }
        dailyTasks.remove(key);
        itr.remove();
    }

    // returns a list of today's tasks
    public ArrayList<String> getTasks() {
        refresh();
        return order;
    }

    public String getTask(String key) {
        return dailyTasks.getTask(key);
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
            loadFirebase();
        }
    }
    //#endregion

    //#region firebase control
    private void loadFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // load lists
        childRef = database.getReference("lists");
        childRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ListOfTasks tasks = dataSnapshot.getValue(ListOfTasksFire.class);
                TasksManager.this.put(dataSnapshot.getKey(), tasks);
                refresh();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                refresh();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                TasksManager.super.remove(dataSnapshot.getKey());
                refresh();
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
        setDirectory(c);
        dailyTasks = new DailyTaskLocal(directory);
        for(String filename : listDirectory.list()) {
            ListOfTasksLocal tasks = new ListOfTasksLocal(listDirectory, filename);
            this.put(filename, tasks);
            order.add(filename);
        }
    }

    private File[] listsFiles() {
        return listDirectory.listFiles();
    }

    private void setDirectory(Context c) {
        directory = new File(c.getFilesDir(), "tasks");

        // creates the directory if not present yet
        directory.mkdir();

        listDirectory = new File(directory, "lists");
        listDirectory.mkdir();
    }
    //#endregion

}
