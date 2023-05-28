package com.example.telechat.models;

public class Doctor {
    public String uid;
    public String name;
    public String email;
    public String profession;
    public String image;
    public String date;
    public String password;

    public Doctor() {}

    public Doctor(String uid, String name, String email, String profession, String image, String date, String password) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profession = profession;
        this.image = image;
        this.date = date;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}