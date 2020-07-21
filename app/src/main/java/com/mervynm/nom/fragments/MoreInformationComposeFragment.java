package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mervynm.nom.R;
import com.mervynm.nom.models.Location;
import com.mervynm.nom.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoreInformationComposeFragment extends Fragment {

    TextView textViewLocation;
    EditText editTextRecipe;
    EditText editTextPrice;
    Button buttonPost;
    File photoFile;
    String description;
    Boolean homemade;
    Location postLocation;
    String recipeUrl;
    double priceDouble;

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
        initializePlaces();
        setUpAutocompleteSupportFragment();
        setupOnClickListener();
    }

    private void setupVariables(View view) {
        textViewLocation = view.findViewById(R.id.textViewLocation);
        editTextRecipe = view.findViewById(R.id.editTextRecipe);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonPost = view.findViewById(R.id.buttonPost);
    }

    private void initializePlaces() {
        Places.initialize(Objects.requireNonNull(getContext()), getResources().getString(R.string.api_key));
    }

    private void setUpAutocompleteSupportFragment() {
        assert getFragmentManager() != null;
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                Log.i("MoreInfoCompose", "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getRating() + ", " + place.getPriceLevel());
                textViewLocation.setText(String.format("Using location: %s", place.getName()));
                postLocation = createLocation(place);
            }
            @Override
            public void onError(@NotNull Status status) {
                Log.i("MoreInfoCompose", "An error occurred: " + status);
            }
        });
    }

    private Location createLocation(Place place) {
        Location location = new Location();
        location.setName(place.getName());
        location.setAddress(place.getAddress());
        if (place.getRating() != null) {
            location.setRating(place.getRating());
        }
        if (place.getPriceLevel() != null) {
            location.setPriceLevel(place.getPriceLevel());
        }
        return location;
    }

    private void setupOnClickListener() {
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfInfoInputtedIsCorrect();
            }
        });
    }

    private void checkIfInfoInputtedIsCorrect() {
        boolean location = false;
        boolean recipe = false;
        boolean price = false;
        if (!textViewLocation.getText().toString().isEmpty()) {
            location = true;
        }
        if (!editTextRecipe.getText().toString().isEmpty()) {
            if (URLUtil.isValidUrl(editTextRecipe.getText().toString())) {
                recipe = true;
                recipeUrl = editTextRecipe.getText().toString();
            }
            else {
                Toast.makeText(getContext(), "Invalid recipe URL", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (!editTextPrice.getText().toString().isEmpty()) {
            String pattern = "^([1-9]{1}[0-9]*|0{0,2})(\\.?)(\\d\\d?)$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(editTextPrice.getText().toString());
            if (m.find()) {
                priceDouble = Double.parseDouble(editTextPrice.getText().toString());
                price = true;
            }
            else {
                Toast.makeText(getContext(), "Invalid price, please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        createPost(location, recipe, price);
    }

    private void createPost(boolean location, boolean recipe, boolean price) {
        Post post = new Post();
        post.setAuthor(ParseUser.getCurrentUser());
        post.setImage(new ParseFile(photoFile));
        post.setKeyDescription(description);
        post.setHomemade(homemade);
        if (location) {
            if (saveLocation()) {
                post.setLocation(postLocation);
            }
            else {
                Toast.makeText(getContext(), "didn't work", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (recipe) {
            post.setRecipeUrl(recipeUrl);
        }
        if (price) {
            post.setPrice(priceDouble);
        }
        savePost(post);
    }

    private boolean saveLocation() {
        final boolean[] save = {true};
        postLocation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MoreInfoCompose", "Issue saving location", e);
                    Toast.makeText(getContext(), "Issue saving location", Toast.LENGTH_SHORT).show();
                    save[0] = false;
                    return;
                }
                Log.i("MoreInfoCompose", "Location successfully saved");
            }
        });
        return save[0];
    }

    private void savePost(Post post) {
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MoreInfoCompose", "Issue saving post", e);
                    Toast.makeText(getContext(), "Issue saving post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("MoreInfoCompose", "Post successsfully saved");
            }
        });
        goToHome();
    }

    private void goToHome() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new HomeFragment()).commit();
    }

}
