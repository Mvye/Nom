package com.mervynm.nom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mervynm.nom.fragments.ComposeFragment;
import com.mervynm.nom.fragments.HomeFragment;
import com.mervynm.nom.fragments.LogoutDialogFragment;
import com.mervynm.nom.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationBar;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onStart() {
        super.onStart();
        View profile = findViewById(R.id.action_profile);
        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showLogOutDialog();
                return true;
            }
        });
    }

    private void showLogOutDialog() {
        LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();
        logoutDialogFragment.show(fragmentManager, "fragment_logout_dialog");
    }

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
                Fragment fragment = new HomeFragment();
                if (item.getItemId() == R.id.action_home) {
                    fragment = new HomeFragment();
                }
                else if (item.getItemId() == R.id.action_compose) {
                    fragment = new ComposeFragment();
                }
                else if (item.getItemId() == R.id.action_profile) {
                    fragment = new ProfileFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment).addToBackStack("").commit();
                return true;
            }
        });
        bottomNavigationBar.setSelectedItemId(R.id.action_home);
    }
}