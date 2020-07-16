package com.mervynm.nom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mervynm.nom.R;

import java.io.File;

public class MoreInformationComposeFragment extends Fragment {

    EditText editTextLocation;
    EditText editTextRecipe;
    EditText editTextPrice;
    Button buttonPost;
    File photoFile;
    String description;
    Boolean homemade;

    public MoreInformationComposeFragment() {}

    public static MoreInformationComposeFragment newInstance(File photoFile, String description, Boolean homemade) {
        MoreInformationComposeFragment moreInformationComposeFragment = new MoreInformationComposeFragment();
        Bundle args = new Bundle();
        args.putSerializable("image", photoFile);
        args.putString("description", description);
        args.putBoolean("homemade", homemade);
        moreInformationComposeFragment.setArguments(args);
        return moreInformationComposeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        photoFile = (File) getArguments().getSerializable("image");
        description = getArguments().getString("description");
        homemade = getArguments().getBoolean("homemade");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_information_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVariables(view);
        setupOnClickListener();
    }

    private void setupVariables(View view) {
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextRecipe = view.findViewById(R.id.editTextRecipe);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonPost = view.findViewById(R.id.buttonPost);
    }

    private void setupOnClickListener() {
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });
    }

    private void createPost() {
        goToHome();
    }

    private void goToHome() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new HomeFragment()).commit();
    }

}
