package com.patrickgrady.todayslists.Managers.Objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TasksListManager extends ArrayList<String> {

    private File directory;
    private String filename;

    public TasksListManager(File d, String key) {
        directory = d;
        filename = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setAll(List<String> tasks) {
        this.clear();
        this.addAll(tasks);
    }

    @Override
    public boolean  addAll(Collection<? extends String> tasks) {
        boolean output = super.addAll(tasks);
        writeListToFile();

        return output;
    }

    @Override
    public boolean add(String task) {
        boolean output = super.add(task);

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(directory, filename),true));
            writer.print("````" + task);
            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return output;
    }

    @Override
    public void add(int p, String task) {
        super.add(p, task);
        writeListToFile();
    }

    @Override
    public String set(int position, String task) {
        String t = super.set(position, task);
        writeListToFile();

        return t;
    }

    @Override
    public String remove(int p) {
        String output = super.remove(p);
        writeListToFile();

        return output;
    }

    @Override
    public boolean isEmpty() {
        if(super.isEmpty()) {
            return true;
        }

        for(String t : this) {
            if(!t.equals("")) {
                return false;
            }
        }

        return true;
    }

    public void sortInPlace() {
        Collections.sort(this);
        writeListToFile();
    }

    public void writeListToFile() {
        try {

            PrintWriter writer = new PrintWriter(new File(directory, filename));

            for (String el : this) {
                writer.print("````" + el);
            }

            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            Scanner scanner = new Scanner(new File(directory, filename)).useDelimiter("````");
            while(scanner.hasNext()) {
                super.add(scanner.next());
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
