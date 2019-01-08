package com.patrickgrady.todayslists.Objects.local;

import com.patrickgrady.todayslists.Objects.ListOfTasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ListOfTasksLocal extends ListOfTasks {

    private File directory;
    private String filename;
    private ArrayList<ListElement> tasks;

    public ListOfTasksLocal (File d, String key) {
        directory = d;
        filename = key;
        tasks = new ArrayList<>();
    }

    @Override
    public void add(String task) {
        ListElement newElement = createElement(task);
        tasks.add(newElement);

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(directory, filename),true));
            String taskString = task + "```" + newElement.created + "```" + newElement.modified;
            writer.print("````" + task);
            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void add(int p, String task) {
        tasks.add(p, createElement(task));
        writeListToFile();
    }

    @Override
    public boolean contains(String task) {
        for(ListElement e : tasks) {
            if(e.value.equals(task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(int index) {
        return tasks.get(index).value;
    }

    @Override
    public String getKey() {
        return filename;
    }

    @Override
    public void set(int position, String task) {
        ListElement oldTask = tasks.get(position);
        oldTask.value = task;
        oldTask.modified = System.currentTimeMillis();
        writeListToFile();
    }

    @Override
    public void move(int start, int end) {
        ListElement task = tasks.remove(start);
        tasks.add(end, task);
        writeListToFile();
    }

    @Override
    public int size() {
        return tasks.size();
    }

    @Override
    public ArrayList<String> getUnderlyingElements() {
        ArrayList<String> values = new ArrayList<>();
        for(ListElement e : tasks) {
            values.add(e.value);
        }
        return values;
    }

    @Override
    public String remove(int p) {
        String output = tasks.remove(p).value;
        writeListToFile();

        return output;
    }

    @Override
    public boolean isEmpty() {
        if(tasks.isEmpty()) {
            return true;
        }

        for(ListElement task : tasks) {
            if(!task.value.equals("")) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void sortInPlace(String op) {
        Collections.sort(tasks, (a,b) -> a.value.compareTo(b.value));
        writeListToFile();
    }

    public void writeListToFile() {
        try {

            PrintWriter writer = new PrintWriter(new File(directory, filename));

            for (ListElement task : tasks) {
                String taskString = task.value + "```" + task.created + "```" + task.modified;
                writer.print("````" + taskString);
            }

            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        tasks.clear();
        try {
            Scanner scanner = new Scanner(new File(directory, filename)).useDelimiter("````");
            while(scanner.hasNext()) {
                String []parts = scanner.next().split("```");
                ListElement task = new ListElement(parts[0], Long.parseLong(parts[1]), Long.parseLong(parts[2]));
                tasks.add(task);
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private ListElement createElement(String task) {
        return new ListElement(task, System.currentTimeMillis());
    }
}
