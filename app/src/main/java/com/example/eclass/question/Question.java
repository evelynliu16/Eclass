package com.example.eclass.question;
import java.util.ArrayList;
import java.util.UUID;

public class Question {
    private String name;
    private String title;
    private String description;
    private String answer;
    private String id;

    public Question() {}

    public Question(String name, String title, String description) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public void postAnswer(String answer) {
        if (this.answer == null) {
            this.answer = answer;
        } else {
            this.answer += "\n" + "\n" + answer;
        }
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
