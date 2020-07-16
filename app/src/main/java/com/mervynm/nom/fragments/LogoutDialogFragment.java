package com.mervynm.nom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mervynm.nom.LoginActivity;
import com.mervynm.nom.R;
import com.parse.ParseUser;

import java.util.Objects;

public class LogoutDialogFragment extends DialogFragment {

    Button buttonStaySignedIn;
    Button buttonLogOut;

    public LogoutDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpVariables(view);
        setOnClickListeners();
    }

    private void setUpVariables(View view) {
        buttonStaySignedIn = view.findViewById(R.id.buttonStaySignedIn);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
    }

    private void setOnClickListeners() {
        buttonStaySignedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });
    }

    private void logOutUser() {
        ParseUser.logOut();
        goToMainActivity();
        Toast.makeText(getContext(), "Successfully Logged Out", Toast.LENGTH_SHORT).show();
    }

    private void goToMainActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        Objects.requireNonNull(getActivity()).finish();
    }
}