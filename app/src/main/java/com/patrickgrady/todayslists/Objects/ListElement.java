package com.patrickgrady.todayslists.Objects;

public abstract class ListElement {

    public static final String ROOT = "root";
    private ListProps props;

    protected ListElement() {}

    public ListProps getProps() {
        return props;
    }

    //#region public interface
    public <T extends Comparable> void addChild(T type) { addChild("", type); }
    public abstract <T extends Comparable> void addChild(String name, T type);
    public abstract boolean listExists(String key);
    public abstract void move(int start, int finish);
    public abstract void removeChild(String key);
    public void setName(String name) { this.props.name = name; }
    public <T extends Comparable> void setType(T type) {}
    //#endregion

    //#region props manipulation
    protected void setProps(ListProps p) {
        props = p;
    }
    //#endregion
}