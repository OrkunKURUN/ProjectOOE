package com.omo.projectomo;

public class User {
    public String userId, name, password, e_mail;

    public User(){}

    public User(String userId, String name, String password, String e_mail){
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.e_mail = e_mail;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getE_mail() { return e_mail; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setE_mail(String e_mail) { this.e_mail = e_mail; }
}
