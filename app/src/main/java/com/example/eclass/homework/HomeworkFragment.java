package com.example.eclass.homework;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eclass.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeworkFragment extends Fragment {

    CalendarView calender;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_homework, container, false);
        final TextView content = root.findViewById(R.id.hmContent);
        content.setTextColor(Color.BLACK);
        calender = root.findViewById(R.id.hmCalender);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Homework");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final String today = sdf.format(new Date(calender.getDate()));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(today).exists()) {
                    String homework = snapshot.child(today).getValue().toString();
                    content.setText(homework);
                } else {
                    content.setText("None");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /** Set calender view. **/
        calender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            final String date = formatDate(year, month, dayOfMonth);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(date).exists()) {
                        String homework = snapshot.child(date).getValue().toString();
                        content.setText(homework);
                    } else {
                        content.setText("None");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        return root;
    }

    /** Format the date given to the correct form that can be used for search in the database. **/
    private String formatDate(int year, int month, int day) {
        String mon = Integer.toString(month + 1);
        String d = Integer.toString(day);
        if (month < 10) {
            mon = "0" + mon;
        } if (day < 10) {
            d = "0" + d;
        }
        return year + mon + d;
    }

}