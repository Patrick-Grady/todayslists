package com.patrickgrady.todayslists.Objects;


import java.util.ArrayList;

public class ListProps {

    public final static String FOLDER = "folder", STRING = "string";
    //public final static Integer FOLDER = 0, STRING = 1;

    public String name;
    public String type;
    public Long created;
    public ArrayList<String> children;

    public ListProps() {
        this("","");
    }

    public ListProps(String name, String type) {
        this(name, type, new ArrayList<>());
    }

    public ListProps(String name, String type, ArrayList<String> children) {
        this.name = name;
        this.type = type;
        this.created = System.currentTimeMillis();
        this.children = children;
    }
}

