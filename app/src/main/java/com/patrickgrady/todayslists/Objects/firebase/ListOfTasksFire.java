package com.patrickgrady.todayslists.Objects.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.patrickgrady.todayslists.Objects.ListOfTasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListOfTasksFire extends ListOfTasks {
    private static final String PATH = "lists/";


    private String key;
    private HashMap<String, ListElement> elements;
    private ArrayList<String> order;
    private DatabaseReference elementsRef, orderRef;

    //#region set-up
    public ListOfTasksFire(String key) {
        this.key    = key;
        elements    = new HashMap<>();
        order       = new ArrayList<>();
        setUpDatabaseRef();
    }
    //#endregion

    //#region firebase listeners
    private void setUpDatabaseRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        elementsRef = database.getReference(PATH + key + "/elements");
        elementsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                ListElement element = dataSnapshot.getValue(ListElement.class);
                elements.put(key, element);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                ListElement element = dataSnapshot.getValue(ListElement.class);
                elements.put(key, element);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                elements.remove(key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read child value.", error.toException());
            }
        });

        orderRef = database.getReference(PATH + key + "/elements");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                order = (ArrayList<String>) dataSnapshot.getValue(List.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    //#endregion


    //#region interface methods
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String get(int index) {
        return elements.get(order.get(index)).value;
    }

    public void add(String task) {
        add(order.size(), task);
    }

    public void add(int index, String task) {
        String newKey = elementsRef.push().getKey();
        ListElement e = new ListElement(task, System.currentTimeMillis());
        elementsRef.child(newKey).setValue(e);
        order.add(index, newKey);
    }

    public void set(int index, String task) {
        String setKey = order.get(index);
        ListElement e = new ListElement(task, elements.get(setKey).created, System.currentTimeMillis());
        elementsRef.child(setKey).setValue(e);
    }

    public String remove(int index) {
        String removeKey = order.get(index);
        String output = elements.get(removeKey).value;
        elementsRef.child(removeKey).removeValue();

        return output;
    }

    public boolean contains(String v) {
        for(ListElement e : elements.values()) {
            if(e.value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    public void move(int start, int end) {
        String elementKey = order.remove(start);
        order.add(end, elementKey);
        orderRef.setValue(order);
    }

    public boolean isEmpty() {
        if(elements.isEmpty()) {
            return true;
        }

        for(String k : elements.keySet()) {
            if(elements.get(k) != null) {
                return false;
            }
            else if(!elements.get(k).equals("")) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        return order.size();
    }

    public ArrayList<String> getUnderlyingElements() {
        ArrayList<String> list = new ArrayList<>();
        for(String s : order) {
            list.add(elements.get(s).value);
        }
        return list;
    }
    //#endregion

    public void sortInPlace(String op) {
        switch(op) {
            case "value":
                Collections.sort(order, (a, b) -> (elements.get(a).value).compareTo(elements.get(b).value));
                break;
            case "created":
                Collections.sort(order, (a, b) -> (elements.get(a).created).compareTo(elements.get(b).created));
                break;
            case "modified":
                Collections.sort(order, (a, b) -> (elements.get(a).modified).compareTo(elements.get(b).modified));
                break;
        }
        orderRef.setValue(order);
    }


}
