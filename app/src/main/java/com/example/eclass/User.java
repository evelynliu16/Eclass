package com.example.eclass;

import java.util.ArrayList;

public class User {

    private String name;
    private String phone;
    private String password;
    private ArrayList<String> questions;

    public User(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public void postQuestion(String question) {
        questions.add(question);
    }
}
