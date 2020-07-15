package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mervynm.nom.R;

public class ProfileFragment extends Fragment {

    ImageView imageViewProfilePicture;
    TextView textViewUsername;
    RecyclerView recyclerViewPosts;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVariables(view);
    }

    private void setupVariables(View view) {
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
    }
}