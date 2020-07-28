package com.mervynm.nom.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.mervynm.nom.R;

import java.util.Objects;

public class LocationDialogFragment extends DialogFragment {
    ImageView imageViewLocationPicture;
    TextView textViewLocationName;
    RatingBar ratingBarLocationRating;
    TextView textViewLocationRating;
    TextView textViewLocationAddress;
    TextView textViewLocationPriceLevel;
    String locationName;
    double locationRating;
    String locationAddress;
    int locationPriceLevel;
    String pictureUrl;

    public LocationDialogFragment() {}

    public static LocationDialogFragment newInstance(String name, double rating, String address, int priceLevel, String pictureUrl) {
        LocationDialogFragment fragment = new LocationDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putDouble("rating", rating);
        args.putString("address", address);
        args.putInt("priceLevel", priceLevel);
        args.putString("pictureUrl", pictureUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationName = getArguments().getString("name");
            locationRating = getArguments().getDouble("rating");
            locationAddress = getArguments().getString("address");
            locationPriceLevel = getArguments().getInt("priceLevel");
            pictureUrl = getArguments().getString("pictureUrl");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpVariables(view);
        bindLocationInfo();
    }

    private void setUpVariables(View view) {
        imageViewLocationPicture = view.findViewById(R.id.imageViewLocationPicture);
        textViewLocationName = view.findViewById(R.id.textViewLocationName);
        ratingBarLocationRating = view.findViewById(R.id.ratingBarLocationRating);
        textViewLocationRating = view.findViewById(R.id.textViewLocationRating);
        textViewLocationAddress = view.findViewById(R.id.textViewLocationAddress);
        textViewLocationPriceLevel = view.findViewById(R.id.textViewLocationPriceLevel);
    }

    @SuppressLint("SetTextI18n")
    private void bindLocationInfo() {
        if (!pictureUrl.equals("")) {
            imageViewLocationPicture.setVisibility(View.VISIBLE);
            Glide.with(Objects.requireNonNull(getContext())).load(pictureUrl)
                                                            .override(Target.SIZE_ORIGINAL)
                                                            .into(imageViewLocationPicture);
        }
        else {
            imageViewLocationPicture.setVisibility(View.GONE);
        }
        textViewLocationName.setText(locationName);
        float rating = (float) locationRating;
        ratingBarLocationRating.setRating(rating);
        textViewLocationRating.setText(Double.toString(locationRating));
        textViewLocationAddress.setText(locationAddress);
        StringBuilder priceLevelDollars = new StringBuilder();
        for (int i = 0; i < locationPriceLevel; i++) {
            priceLevelDollars.append("$");
        }
        textViewLocationPriceLevel.setText(priceLevelDollars);
    }
}