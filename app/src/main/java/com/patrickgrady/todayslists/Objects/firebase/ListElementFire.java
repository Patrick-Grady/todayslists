package com.patrickgrady.todayslists.Objects.firebase;

import android.support.annotation.NonNull;
import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.patrickgrady.todayslists.Adapters.MainAdapter;
import com.patrickgrady.todayslists.Managers.ListManager;
import com.patrickgrady.todayslists.Objects.ListElement;
import com.patrickgrady.todayslists.Objects.ListOfTasks;
import com.patrickgrady.todayslists.Objects.ListProps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ListElementFire extends ListElement {

    private static final String PATH = "lists/";


    private DatabaseReference propsRef;

    public ListElementFire(String key, MainAdapter adapter) {
        setUpDatabaseRef(key, adapter);
    }

    //#region firebase listeners
    private void setUpDatabaseRef(String key, MainAdapter adapter) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        propsRef = database.getReference(PATH + key);

        if(key.equals(ROOT)) {
            propsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot == null) {
                        createRoot();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("now what");
                }
            });
        }


        propsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ListElementFire.super.setProps(dataSnapshot.getValue(ListProps.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    //#endregion

    @Override
    public <T extends Comparable> void addChild(String name, T type) {
        // create child in firebase
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference(PATH).push();
        childRef.setValue(new ListProps(name, (String) type));

        // add key to current element's list of children
        ListProps newProps = getProps();
        newProps.children.add(childRef.getKey());
        propsRef.setValue(newProps);
    }

    @Override
    public boolean listExists(String key) {
        return true;
    }

    @Override
    public void move(int start, int finish) {
        // change ordering of element's children
        ArrayList<String> order = getProps().children;
        order.add(finish, order.remove(start));
        propsRef.setValue(getProps());
    }

    @Override
    public void removeChild(String key) {
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference(PATH+key);
        childRef.removeValue();

        ArrayList<String> order = getProps().children;
        order.remove(key);
        propsRef.setValue(getProps());
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        propsRef.setValue(getProps());
    }

    public void sortInPlace(String op) {
        ArrayList<String> order = getProps().children;
        switch(op) {
            case "value":
                Collections.sort(order, (a, b) -> (LM().getProps(a).name).compareTo(LM().getProps(b).name));
                break;
            case "created":
                Collections.sort(order, (a, b) -> (LM().getProps(a).created).compareTo(LM().getProps(b).created));
                break;
        }
        propsRef.setValue(getProps());
    }

    private ListManager LM() {
        return ListManager.getInstance(ListManager.FIREBASE);
    }

    private void createRoot() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        propsRef = database.getReference(PATH + ROOT);
        propsRef.setValue(new ListProps("root", ListProps.FOLDER));
    }
}
