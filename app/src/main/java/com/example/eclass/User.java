package com.example.eclass;

import java.util.ArrayList;

public class User {

    private String name;
    private String phone;
    private boolean instructor;
    private ArrayList<String> questions;

    public User() {}

    public User(String name, String phone, boolean instructor) {
        this.name = name;
        this.phone = phone;
        this.instructor = instructor;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isInstructor() {
       return this.instructor;
    }


    public void postQuestion(String question) {
        if (questions == null) {
            ArrayList<String> questions = new ArrayList<>();
            this.questions = questions;
            questions.add(question);
        } else {
            questions.add(question);
        }
    }

    public ArrayList<String> getQuestions() {
        return this.questions;
    }
}
