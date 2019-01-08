package com.patrickgrady.todayslists.Objects;

import java.util.ArrayList;

public abstract class ListOfTasks {
    /*

                    Return     Function    Parameters

     */

    public abstract void       add          (String task);
    public abstract void       add          (int index, String task);
    public abstract boolean    contains     (String task);
    public abstract String     get          (int index);
    public abstract String     getKey       ();
    public abstract boolean    isEmpty      ();
    public abstract void       move         (int start, int end);
    public abstract String     remove       (int index);
    public abstract void       set          (int index, String task);
    public abstract int        size         ();
    public abstract void       sortInPlace  (String option);

    public abstract ArrayList<String> getUnderlyingElements();

    protected class ListElement {

        public String value;
        public Long created;
        public Long modified;

        public ListElement(){}

        public ListElement(String v, long c) {
            value = v;
            created = c;
            modified = c;
        }

        public ListElement(String v, long c, long m) {
            value = v;
            created = c;
            modified = m;
        }
    }
}