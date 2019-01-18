package com.patrickgrady.todayslists.Objects.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.patrickgrady.todayslists.Objects.DailyTasks;
import com.patrickgrady.todayslists.Objects.SimpleTime.TimeUnit;

public class DailyTasksFire extends DailyTasks {
    final String PATH = "daily";
    DatabaseReference tasksRef;

    public DailyTasksFire() {
        super();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        tasksRef = database.getReference(PATH);
        tasksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DailyTasksFire.super.putTask(dataSnapshot.getKey(), (DailyTask) dataSnapshot.getValue(DailyTask.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DailyTasksFire.super.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void putTask(String key, DailyTask task) {
        tasksRef.child(key).setValue(task);
    }

    @Override
    public void clear() {
        tasksRef.removeValue();
        super.clear();
    }

    @Override
    public void remove(String key) {
        tasksRef.child(key).removeValue();
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
