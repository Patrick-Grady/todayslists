package com.patrickgrady.todayslists.Managers;


import com.patrickgrady.todayslists.Adapters.MainAdapter;
import com.patrickgrady.todayslists.Objects.ListElement;
import com.patrickgrady.todayslists.Objects.ListProps;
import com.patrickgrady.todayslists.Objects.firebase.ListElementFire;

import java.util.ArrayList;
import java.util.HashMap;

public class ListManager {

    public static void main(String []args) {

    }

    //#region static instance and constants
    public static final int LOCAL_STORAGE = 0, FIREBASE = 1;

    static ListManager instance;

    public static ListManager getInstance(int ...storageType) {
        if(instance == null)
            instance = new ListManager(storageType);
        return instance;
    }

    private ListManager(int ...storageType) {
        this.listMap = new HashMap<>();
        this.focusKey = "root";
        this.storageType = storageType.length == 1 ? storageType[0] : FIREBASE;
    }
    //#endregion

    //#region member variables
    private MainAdapter adapter;
    private HashMap<String, ListElement> listMap;
    private String focusKey;    // key of list returned
    private int storageType;
    //#endregion

    //#region public api

    // set meta elements
    public void setFocusList(String key) throws Exception {
        // assert key is found
        if(listExists(key)) {
            focusKey = key;
        }
        else {
            throw new Exception("List does not exist.");
        }
    }

    public void setAdapter(MainAdapter a) {
        adapter = a;
    }

    public void setStorageType(int type) throws Exception {
        if(type != LOCAL_STORAGE && type != FIREBASE) {
            throw new Exception("not a valid storage type");
        }
        storageType = type;
    }

    // get info
    public ListProps getProps(String key) { return get(key).getProps(); }
    public ArrayList<String> getElementKeys() { return getElementKeys(focusKey); }

    // list manipulation
    public void addNewString() { get(focusKey).addChild(ListProps.STRING); }
    public void addNewFolder() { get(focusKey).addChild(ListProps.FOLDER); }
    public void moveChild(int start, int end) { get(focusKey).move(start, end); }
    public void renameChild(String key, String name) { get(key).setName(name); }
    public void removeAllChildren() { removeAllChildren(focusKey); }

    public void changeType(String key) {
        String newType = getProps(key).type.equals(ListProps.FOLDER) ? ListProps.STRING : ListProps.FOLDER;
        removeAllChildren(key);
        get(key).setType(newType);
    }

    public <T extends Comparable> void removeChild(String key) {
        removeAllChildren(key);
        get(focusKey).removeChild(key);
    }
    //#endregion

    //#region protected members
    protected ListElement get(String key) {
        if(listMap.containsKey(key)) {
            return listMap.get(key);
        }
        ListElement element = getNewLE(key);
        listMap.put(key, element);
        return element;
    }
    //#endregion

    //#region private members
    public ArrayList<String> getElementKeys(String key) {
        ListElement current = get(key);
        return current.getProps().children;
    }

    private ListElement getNewLE(String key) {
        if(storageType == LOCAL_STORAGE) {
            return new ListElementFire(key, adapter);
        }
        else if(storageType == FIREBASE) {
            return new ListElementFire(key, adapter);
        }
        return new ListElementFire(key, adapter);
    }

    private boolean listExists(String key) {
        if(storageType == LOCAL_STORAGE) {
            // get from directory listing
        }
        else if(storageType == FIREBASE) {
            // get from firebase listing
            get(key);
        }
        return listMap.containsKey(key);
    }

    private <T extends Comparable> void removeAllChildren(String key) {
        T type = (T) getProps(key).type;    // undecided on final type of type variable

        if(type.equals(ListProps.FOLDER)) {
            for(String k : getElementKeys(key)) {
                removeChild(k);
            }
        }
    }



    //#endregion
}