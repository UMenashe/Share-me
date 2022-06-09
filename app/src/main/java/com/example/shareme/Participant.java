package com.example.shareme;

public class Participant {
    private String name;
    private String imageUrl;
    private String role;

    public Participant(String name, String imageUrl, String role) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
