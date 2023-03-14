package com.example.socialmediaapplication.service.board;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.example.socialmediaapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Created by LittleDuck
 * Name of project: SocialMediaApplication
 */

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Profile Activity");
        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);
        actionBar.setTitle("Home Activity");

    }

    private final BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_home:
                    actionBar.setTitle("Home Page");

                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile Page");

                    return true;

                case R.id.nav_addblogs:
                    actionBar.setTitle("Dashboard Posts");

                    return true;

            }
            return false;
        }
    };
}
