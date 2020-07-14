package com.example.eclass;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Question {
    private String name;
    private String title;
    private String description;
    private String answer;

    public Question() {}

    public Question(String name, String title, String description) {
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public void postAnswer(String answer) {
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean answered() {
        return answer != null;
    }
}
