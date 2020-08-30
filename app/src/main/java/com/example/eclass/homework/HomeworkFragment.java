package com.example.eclass.homework;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eclass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.eclass.MainActivity.fab;
import static com.example.eclass.MainActivity.user;


public class HomeworkFragment extends Fragment {

    CalendarView calender;
    Dialog myDialog;
    private DatabaseReference mDatabase;

    String date;
    String homework1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_homework, container, false);
        final TextView content = root.findViewById(R.id.hmContent);
        content.setTextColor(Color.BLACK);
        calender = root.findViewById(R.id.hmCalender);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Homework");
        myDialog = new Dialog(requireContext());

        if (user != null && user.isInstructor()) {
            fab.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final String today = sdf.format(new Date(calender.getDate()));
        date = today;
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(today).exists()) {
                    String homework = snapshot.child(today).getValue().toString();
                    content.setText(homework);
                    homework1 = homework;
                } else {
                    String hw = "None";
                    content.setText(hw);
                    homework1 = hw;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set calender view. //
        calender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = formatDate(year, month, dayOfMonth);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(date).exists()) {
                        homework1 = snapshot.child(date).getValue().toString();
                        content.setText(homework1);
                    } else {
                        content.setText("None");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        fab.setOnClickListener(v -> showPopup());

        return root;
    }


    public void showPopup() {
        myDialog.setContentView((R.layout.homework_popup));

        Window window = myDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Initialize the elements of our window, install the handler
        EditText hw = myDialog.findViewById(R.id.quesPopUpHw);
        TextView day = myDialog.findViewById(R.id.quesPopUpDate);

        day.setText(date);
        hw.setText(homework1);

        Button save = myDialog.findViewById(R.id.quesPopUpSave);
        Button close = myDialog.findViewById(R.id.quesPopUpClose);
        save.setOnClickListener(v12 -> {
            mDatabase.child(date).setValue(hw.getText().toString());
            myDialog.dismiss();
        });


        close.setOnClickListener(v1 -> myDialog.dismiss());

        myDialog.show();
    }

    /**
     * Format the date given to the correct form that can be used for search in the database.
     **/
    private String formatDate(int year, int month, int day) {
        String mon = Integer.toString(month + 1);
        String d = Integer.toString(day);
        if (month < 10) {
            mon = "0" + mon;
        }
        if (day < 10) {
            d = "0" + d;
        }
        return year + mon + d;
    }

}