package com.example.eclass;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    static DatabaseReference mDatabase;
    private TextView name;
    Button logOut;
    ImageView profilePic;
    public static FloatingActionButton fab;
    public static Toolbar toolbar;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.navName);
        profilePic = header.findViewById(R.id.navPic);

        fab = findViewById(R.id.fab);

        logOut = header.findViewById(R.id.navLogOut);
        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent toMain = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(toMain);
        });

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations. //
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_homework, R.id.nav_question_list, R.id.nav_recording)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * Create toolbar.
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem add = menu.findItem(R.id.actionProfile);
        MenuItem logOut = menu.findItem(R.id.actionLogout);

        // Log out on click listener. //
        logOut.setOnMenuItemClickListener(item -> {
            FirebaseAuth.getInstance().signOut();
            Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(toLogin);
            return true;
        });

        // Add on click listener. //
        add.setOnMenuItemClickListener(item -> {
            ProfileFragment fragment = new ProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment, fragment);
            transaction.commit();
            return true;
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String phoneNumber = firebaseUser.getPhoneNumber();
            mDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.child(phoneNumber).getValue(User.class);
                    name.setText(user.getName());
                    if (user.getProfilePic() != null) {
                        Picasso.get().load(user.getProfilePic()).into(profilePic);
                    }
                    if (!user.isInstructor()) {
                        fab.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
