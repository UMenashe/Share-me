package com.example.shareme;

public class ListItemTarget {
    private String name;
    private int targetCount;
    private int currentCount;
    private String id;
    private boolean isComplete;

    public ListItemTarget(String name, int targetCount, int currentCount, String id, boolean isComplete) {
        this.name = name;
        this.targetCount = targetCount;
        this.currentCount = currentCount;
        this.id = id;
        this.isComplete = isComplete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
