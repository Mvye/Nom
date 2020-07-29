package com.mervynm.nom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.mervynm.nom.fragments.ComposeFragment;

import java.util.Objects;

public class ComposeActivity extends AppCompatActivity {

    MaterialToolbar toolbarBackButton;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 1 ) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        setUpToolbar();
        showComposeFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        toolbarBackButton = findViewById(R.id.toolbarBackButton);
        setSupportActionBar(toolbarBackButton);
        Drawable backArrowWhite = getResources().getDrawable(R.drawable.ic_back);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backArrowWhite);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showComposeFragment() {
        fragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, new ComposeFragment()).addToBackStack("").commit();
    }
}