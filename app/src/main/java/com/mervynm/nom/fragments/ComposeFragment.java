package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.mervynm.nom.R;

public class ComposeFragment extends Fragment {

    ImageView imageViewPicture;
    Button buttonTakePicture;
    EditText editTextDescription;
    Switch switchHomemade;
    Button buttonNext;

    public ComposeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVariables(view);
        setupOnClickListeners();
    }

    private void setupVariables(View view) {
        imageViewPicture = view.findViewById(R.id.imageViewPicture);
        buttonTakePicture = view.findViewById(R.id.buttonTakePicture);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        switchHomemade = view.findViewById(R.id.switchHomemade);
        buttonNext = view.findViewById(R.id.buttonNext);
    }

    private void setupOnClickListeners() {
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "this is supposed to open the camera", Toast.LENGTH_SHORT).show();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMoreInformationComposeFragment();
            }
        });
    }

    private void goToMoreInformationComposeFragment() {
        MoreInformationComposeFragment moreInformationComposeFragment = new MoreInformationComposeFragment();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, moreInformationComposeFragment).commit();
    }

}