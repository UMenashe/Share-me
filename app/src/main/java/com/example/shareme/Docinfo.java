package com.example.shareme;

public class Docinfo {
    private String type; // targetList || generalList
    private String id;
    private String createTime;
    private String lastUpdate;
    private String title;
    private String owner;

    public Docinfo() { }
    public Docinfo(String type, String id, String createTime, String lastUpdate, String title, String owner) {
        this.type = type;
        this.id = id;
        this.createTime = createTime;
        this.lastUpdate = lastUpdate;
        this.title = title;
        this.owner = owner;
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
