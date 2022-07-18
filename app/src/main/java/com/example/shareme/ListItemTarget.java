package com.example.shareme;

import java.util.Map;

public class ListItemTarget {
    private String name;
    private int targetCount;
    private int currentCount;
    private String id;
    private boolean isComplete;
    private Map<String, Integer> countPerUser;

    public ListItemTarget(){}
    public ListItemTarget(String name, int targetCount, String id,Map<String, Integer> countPerUser) {
        this.name = name;
        this.targetCount = targetCount;
        this.currentCount = 0;
        this.id = id;
        this.isComplete = false;
        this.countPerUser = countPerUser;
    }

    public Map<String, Integer> getCountPerUser() {
        return countPerUser;
    }

    public void setCountPerUser(Map<String, Integer> countPerUser) {
        this.countPerUser = countPerUser;
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

    public boolean isTargetComplete(){
        return this.currentCount == this.targetCount;
    }

    public void addCount(){
            this.currentCount += 1;
    }

    public void subtractCount() {
        if (this.currentCount != 0) {
            this.currentCount -= 1;
        }
    }

    public boolean checkUserCount(String uid){
        return this.countPerUser.get(uid) == 0;
    }

    public String itemToCopyTxt(){
        String txt = "";

        txt = txt + "● " + this.name +"- " + " " + "יעד" + ":" + this.targetCount + " " + " | " + "כמות" +":"+this.currentCount + "\n" + "משתתפים בפריט";
        for (Map.Entry<String,Integer> entry : this.countPerUser.entrySet()){
            txt = txt + "\n" + "- " + entry.getKey() + ":" + entry.getValue();
        }
        return txt;
    }

    @Override
    public String toString() {
        return "ListItemTarget{" +
                "name='" + name + '\'' +
                ", targetCount=" + targetCount +
                ", currentCount=" + currentCount +
                ", id='" + id + '\'' +
                ", isComplete=" + isComplete +
                ", countPerUser=" + countPerUser +
                '}';
    }
}
