package com.mervynm.nom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVariables();
        setupToolbar();
        setUpBottomNavigationBar();
    }

    private void setupVariables() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationBar = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        toolbar.setTitle("Nom");
    }

    private void setUpBottomNavigationBar() {
        bottomNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return true;
            }
        });
    }
}