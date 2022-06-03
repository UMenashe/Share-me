package com.example.shareme;

import java.util.HashMap;
import java.util.Map;

public class Docinfo {
    private String type; // targetList || generalList
    private String id;
    private String createTime;
    private String lastUpdate;
    private String title;
    private String owner;
    private Map<String, Boolean> participants;

    public Docinfo() { }
    public Docinfo(String type, String id, String createTime, String lastUpdate, String title, String owner) {
        this.type = type;
        this.id = id;
        this.createTime = createTime;
        this.lastUpdate = lastUpdate;
        this.title = title;
        this.owner = owner;
        this.participants = new HashMap<>();
        this.participants.put(owner,true);
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Docinfo{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", createTime='" + createTime + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", title='" + title + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }

}
