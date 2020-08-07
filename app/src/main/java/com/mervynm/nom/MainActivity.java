package com.mervynm.nom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mervynm.nom.fragments.ComposeFragment;
import com.mervynm.nom.fragments.HomeFragment;
import com.mervynm.nom.fragments.LogoutDialogFragment;
import com.mervynm.nom.fragments.ProfileFragment;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationBar;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Stack<String> openedFragments;
    String currentFragment;

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 1 ) {
            finish();
        }
        else {
            fragmentManager.popBackStack();
            if (openedFragments.peek() != null) {
                currentFragment = openedFragments.pop();
                if (openedFragments.peek() != null) {
                    if (openedFragments.peek().equals("Home")) {
                        bottomNavigationBar.getMenu().findItem(R.id.action_home).setChecked(true);
                        openedFragments.pop();
                        Log.i("MainActivity", "removed home from backstack");
                    }
                    else if (openedFragments.peek().equals("Profile")) {
                        bottomNavigationBar.getMenu().findItem(R.id.action_profile).setChecked(true);
                        openedFragments.pop();
                        Log.i("MainActivity", "removed profile from backstack");
                    }
                }
                openedFragments.push(currentFragment);
            }
        }
    }

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
    protected void onResume() {
        super.onResume();
        setCheckedBottomNav();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVariables();
        setUpBottomNavigationBar();
    }

    private void setupVariables() {
        openedFragments = new Stack<>();
        bottomNavigationBar = findViewById(R.id.bottom_navigation);
    }

    private void setUpBottomNavigationBar() {
        bottomNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                if (item.getItemId() == R.id.action_home) {
                    fragment = new HomeFragment();
                    openedFragments.push("Home");
                    Log.i("MainActivity", "added home to backstack");
                }
                else if (item.getItemId() == R.id.action_map_view) {
                    Intent i = new Intent(getApplicationContext(), MapViewActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    return true;
                }
                else if (item.getItemId() == R.id.action_compose) {
                    Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    return true;
                }
                else if (item.getItemId() == R.id.action_profile) {
                    fragment = new ProfileFragment();
                    openedFragments.push("Profile");
                    Log.i("MainActivity", "added profile to backstack");
                }
                fragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment).addToBackStack("").commit();
                return true;
            }
        });
        bottomNavigationBar.setSelectedItemId(R.id.action_home);
    }

    private void setCheckedBottomNav() {
        if (openedFragments.peek() != null) {
            if (openedFragments.peek().equals("Home")) {
                bottomNavigationBar.getMenu().findItem(R.id.action_home).setChecked(true);
            }
            else if (openedFragments.peek().equals("Profile")) {
                bottomNavigationBar.getMenu().findItem(R.id.action_profile).setChecked(true);
            }
        }
    }
}